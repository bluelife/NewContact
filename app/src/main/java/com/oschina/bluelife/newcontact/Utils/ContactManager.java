package com.oschina.bluelife.newcontact.Utils;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Data;

import com.oschina.bluelife.newcontact.model.Person;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by slomka.jin on 2016/11/2.
 */

public class ContactManager {
    public static void delete(ContentResolver cr,int id)
    {

        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, ContactsContract.Contacts._ID + "=" + id, null, null);
        while (cur.moveToNext()) {
            try{
                String lookupKey = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.LOOKUP_KEY));
                Uri uri = Uri.withAppendedPath(ContactsContract.
                        Contacts.CONTENT_LOOKUP_URI, lookupKey);
                System.out.println("The uri is " + uri.toString());
                cr.delete(uri, ContactsContract.Contacts._ID + "=" + id, null);
            }
            catch(Exception e)
            {
                System.out.println(e.getStackTrace());
            }
        }
        cur.close();
    }
    public static boolean update(ContentResolver cr, Person person,Context context){
        boolean success=false;
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();


            //update name
            ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                    .withSelection(Data.RAW_CONTACT_ID + " = ? AND "
                            + Data.MIMETYPE + " = ? AND "
                            +Phone.TYPE + " = ? ", new String[] {person.rowId,Phone.CONTENT_ITEM_TYPE,person.phoneLabel})
                    .withValue(Phone.NUMBER,person.phone)
                    .build());

            ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                   .withSelection(Data.RAW_CONTACT_ID + " = ? AND "
                           +Data.MIMETYPE+" = ?", new String[] {person.rowId,StructuredName.CONTENT_ITEM_TYPE})
                   .withValue(StructuredName.DISPLAY_NAME,person.name)
                   .build());

            //update email

            boolean hasEmail=checkEmailExist(context,person.rowId);
            if(!hasEmail){
                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValue(Data.RAW_CONTACT_ID, person.rowId)
                .withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
                .withValue(Email.DATA,person.email)
                        .withValue(Email.TYPE, Email.TYPE_WORK)
                .build());
            }
            else {
                ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                        .withSelection(Data.RAW_CONTACT_ID + " = ? AND "
                                + Data.MIMETYPE + " = ? AND "
                                + Email.TYPE + " = ?", new String[]{person.rowId, Email.CONTENT_ITEM_TYPE, person.emailLabel})
                        .withValue(Email.DATA, person.email)
                        .build());
            }
            if(checkOrgExist(context,person.rowId)) {
                //update org
                ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                        .withSelection(Data.RAW_CONTACT_ID + " = ? AND "
                                + Data.MIMETYPE + " = ?", new String[]{person.rowId, Organization.CONTENT_ITEM_TYPE})
                        .withValue(Organization.COMPANY, person.company)
                        .withValue(Organization.DEPARTMENT, person.department)
                        .withValue(Organization.TITLE, person.title)
                        .build());
            }
            else{
                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValue(Data.RAW_CONTACT_ID, person.rowId)
                .withValue(Data.MIMETYPE,Organization.CONTENT_ITEM_TYPE)
                .withValue(Organization.COMPANY,person.company)
                .withValue(Organization.DEPARTMENT,person.department)
                .withValue(Organization.TITLE,person.title)
                .build());
            }

            //add or update address
            if(checkPostalExist(context,person.rowId)) {
                ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                        .withSelection(Data.RAW_CONTACT_ID + " = ? AND "
                                + Data.MIMETYPE + " = ?", new String[]{person.rowId, StructuredPostal.CONTENT_ITEM_TYPE})
                        .withValue(StructuredPostal.STREET, person.address)
                        .build());
            }
            else{
                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValue(Data.RAW_CONTACT_ID, person.rowId)
                        .withValue(Data.MIMETYPE,StructuredPostal.CONTENT_ITEM_TYPE)
                        .withValue(StructuredPostal.STREET,person.address)
                        .build());
            }

            //add or update note
            if(checkNoteExist(context,person.rowId)) {
                ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                        .withSelection(Data.RAW_CONTACT_ID + " = ? AND "
                                + Data.MIMETYPE + " = ?", new String[]{person.rowId, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE})
                        .withValue(ContactsContract.CommonDataKinds.Note.NOTE, person.extra)
                        .build());
            }
            else{
                ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValue(Data.RAW_CONTACT_ID, person.rowId)
                        .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Note.NOTE,person.extra)
                        .build());
            }
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
            success=true;
        } catch (Exception e) {
            Log.w("UpdateContact", e.getMessage()+"");
            for(StackTraceElement ste : e.getStackTrace()) {
                Log.w("UpdateContact", "\t" + ste.toString());
            }

        }
        return success;
    }

    private static boolean hasValue(String value){
        return value!=null&&!value.equals("");
    }
    public static boolean insert(ContentResolver cr,Person person){
        ArrayList < ContentProviderOperation > ops = new ArrayList < ContentProviderOperation > ();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (hasValue(person.name)) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.DISPLAY_NAME, person.name).build());
        }

        //------------------------------------------------------ Mobile Number
        if (hasValue(person.phone)) {
            Log.w("sssss","add "+person.phone);
            ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, person.phone)
                    .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                    .build());
        }

        if(hasValue(person.homePhone)){
            ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, person.homePhone)
                    .withValue(Phone.TYPE, Phone.TYPE_HOME)
                    .build());
        }


        //------------------------------------------------------ Email
        if (hasValue(person.email)) {
            ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
                    .withValue(Email.DATA, person.email)
                    .withValue(Email.TYPE, Email.TYPE_WORK)
                    .build());
        }
        if(hasValue(person.extra)){
            ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
            .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.Note.NOTE,person.extra)
            .build());
        }
        //------------------------------------------------------address
        if(hasValue(person.address)){
            ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE)
                    .withValue(StructuredPostal.STREET,person.address)
                    .build());
        }
        //------------------------------------------------------website
        if(hasValue(person.url)){
            ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Website.URL,person.url)
                    .build());
            Log.w("ssss","save"+person.url);
        }
        //------------------------------------------------------ Organization

            ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE)
                    .withValue(Organization.COMPANY, person.company)
                    .withValue(Organization.TYPE, Organization.TYPE_WORK)
                    .withValue(Organization.TITLE, person.title)
                    .withValue(Organization.TYPE, Organization.TYPE_WORK)
                    .withValue(Organization.DEPARTMENT,person.department)
                    .withValue(Organization.TYPE,Organization.TYPE_WORK)
                    .build());

        if(hasValue(person.icon)){
            File image = new File(person.icon);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG , 100, stream);


            int size = bitmap.getRowBytes() * bitmap.getHeight();
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            bitmap.copyPixelsToBuffer(byteBuffer);
            byte[] byteArray = stream.toByteArray();
            ops.add(ContentProviderOperation
                    .newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE)
                    .withValue(Photo.PHOTO,byteArray).build());
        }
        boolean isDone=false;
        // Asking the Contact provider to create a new contact
        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
            isDone=true;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return isDone;
    }
    public static boolean contactExists(ContentResolver contentResolver, String name) {
        /*Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(name));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = contentResolver.query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;*/
        String where = Data.DISPLAY_NAME + " = ? ";
        String[] whereParameters = new String[]{name};
        Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
        boolean exist=false;
        if(cursor!=null){
            if(cursor.moveToFirst())
                exist=true;
            cursor.close();
        }
        else
            exist=false;
        return exist;
    }
    public static boolean checkEmailExist(Context context,String id){
        final String[] emailProjection = new String[]{
                Email.DATA,
                Email.TYPE,
                Email.LABEL,
                Email.CONTACT_ID,
        };

        Cursor email = new CursorLoader(context,
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                emailProjection,
                Email.RAW_CONTACT_ID+"= ?",
                new String[]{id},
                null).loadInBackground();
        Log.w("emailcurosr",email.getCount()+"");
        boolean hasEmail=email.getCount()>0;
        email.close();
        return hasEmail;
    }
    public static boolean checkOrgExist(Context context,String id){
        String where = Data.RAW_CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{id,
                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};

        Cursor orgCur = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
        boolean hasOrg=orgCur.getCount()>0;
        orgCur.close();
        return hasOrg;
    }
    public static boolean checkPostalExist(Context context,String id){
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{id,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};

        Cursor addrCur = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
        boolean hasPostal=addrCur.getCount()>0;
        addrCur.close();
        return hasPostal;
    }
    public static boolean checkNoteExist(Context context,String id){
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{id,
                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
        Cursor noteCur = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
        boolean exist=noteCur.getCount()>0;
        noteCur.close();
        return exist;
    }
    public static void updatePhoto(ContentResolver contentResolver, String bitmapPath,String id){
        ContentValues values = new ContentValues();
        int photoRow = -1;
        String where = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] params=new String[]{id,Photo.CONTENT_ITEM_TYPE};
        Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, where, params, null);
        int idIdx = cursor.getColumnIndexOrThrow(Photo.PHOTO_ID);
        if (cursor.moveToFirst()) {
            photoRow = cursor.getInt(idIdx);
        }
        File image = new File(bitmapPath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG , 100, stream);
        cursor.close();

        int size = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(byteBuffer);
        byte[] byteArray = stream.toByteArray();

        values.put(Data.RAW_CONTACT_ID, id);
        values.put(Data.IS_SUPER_PRIMARY, 1);
        values.put(Photo.PHOTO, byteArray);
        values.put(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE);

        Log.w("photo",photoRow+" "+id+"<");
        if (photoRow >= 0) {

            int count=contentResolver.update(ContactsContract.Data.CONTENT_URI, values, Data._ID + " = " + photoRow,null);
            Log.w("int,",count+"");
        } else {
            contentResolver.insert(ContactsContract.Data.CONTENT_URI, values);
        }
        bitmap.recycle();

    }
    public static byte[] openPhoto(ContentResolver contentResolver, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = contentResolver.query(photoUri,
                new String[] {Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return data;
                }
            }
        } finally {
            cursor.close();
        }
        return null;

    }
}
