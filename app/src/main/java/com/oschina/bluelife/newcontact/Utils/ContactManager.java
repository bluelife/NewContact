package com.oschina.bluelife.newcontact.Utils;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Data;

import com.oschina.bluelife.newcontact.model.Person;

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

                //update org
                ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                        .withSelection(Data.RAW_CONTACT_ID + " = ? AND "
                                + Data.MIMETYPE + " = ?", new String[]{person.rowId, Organization.CONTENT_ITEM_TYPE})
                        .withValue(Organization.COMPANY, person.company)
                        .withValue(Organization.DEPARTMENT, person.department)
                        .withValue(Organization.TITLE, person.title)
                        .build());
            }

            ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                            .withSelection(Data.RAW_CONTACT_ID + " = ? AND "
                                    +Data.MIMETYPE+" = ?",new String[]{person.rowId,StructuredPostal.CONTENT_ITEM_TYPE})
                    .withValue(StructuredPostal.STREET,person.address)
                    .build());

            ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
                    .withSelection(Data.RAW_CONTACT_ID + " = ? AND "
                            +Data.MIMETYPE+" = ?",new String[]{person.rowId, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.CommonDataKinds.Note.NOTE,person.extra)
                    .build());
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
}
