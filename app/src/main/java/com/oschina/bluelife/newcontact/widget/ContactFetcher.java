package com.oschina.bluelife.newcontact.widget;

import android.content.ContentUris;
import android.content.Context;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Organization;

import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.oschina.bluelife.newcontact.model.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.provider.ContactsContract.Data;

/**
 * Created by slomka.jin on 2016/11/1.
 */

public class ContactFetcher {
    private final Context context;
    private final String ORDER = "upper(" + Phone.DISPLAY_NAME + ") ASC";
    private String[] projectionFields = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.TIMES_CONTACTED
    };
    private CursorLoader cursorLoader;

    public ContactFetcher(Context c) {
        this.context = c;
        cursorLoader = new CursorLoader(context,
                ContactsContract.Contacts.CONTENT_URI,
                projectionFields, // the columns to retrieve
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "=?", // the selection criteria (none)
                new String[]{"1"}, // the selection args (none)
                ORDER // the sort order (default)
        );
    }

    public ArrayList<Person> fetchAll() {

        ArrayList<Person> listContacts = new ArrayList<>();
        Cursor c = cursorLoader.loadInBackground();

        if (c.moveToFirst()) {

            int idIndex = c.getColumnIndex(ContactsContract.Contacts._ID);
            int nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int timesIndex = c.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED);
            //int rowIdIndex=c.getColumnIndex(ContactsContract.RawContacts._ID);
            do {
                String contactId = c.getString(idIndex);
                //String rowId=c.getString(rowIdIndex);

                int count = c.getInt(timesIndex);
                String contactDisplayName = c.getString(nameIndex);
                Person person = new Person(contactDisplayName, "", "");
                person.id = contactId;
                person.connectCount = count;

                //person.email=email;
                loadPersonListData(person, contactId);
                //contactsMap.put(contactId, person);
                listContacts.add(person);

            } while (c.moveToNext());
        }

        c.close();
        return listContacts;
    }

    private void loadPersonListData(Person person, String contactId) {
        //person.rowId=getRawContactId(contactId);
        //matchContactNumbers(person,contactId);
        //matchOrg(person, contactId);
        //matchNote(person, contactId);
        //matchAddress(person,contactId);
        //matchWebsite(person,contactId);
        matchContactEmails(person, contactId);
    }

    private void loadPersonData(Person person, String contactId) {
        person.rowId = getRawContactId(contactId);
        matchContactNumbers(person, contactId);
        matchOrg(person, contactId);
        matchNote(person, contactId);
        matchAddress(person, contactId);
        matchWebsite(person, contactId);
        matchContactEmails(person, contactId);
    }

    public Person fetchSingle(String name) {
        String where = Data.DISPLAY_NAME + " = ? ";
        String[] whereParameters = new String[]{name};
        CursorLoader cursorLoader = new CursorLoader(context,
                ContactsContract.Contacts.CONTENT_URI,
                projectionFields, // the columns to retrieve
                where, // the selection criteria (none)
                whereParameters, // the selection args (none)
                "upper(" + Phone.DISPLAY_NAME + ") ASC" // the sort order (default)
        );

        Person person = null;
        Cursor c = cursorLoader.loadInBackground();
        if (c != null) {
            if (c.moveToFirst()) {
                int idIndex = c.getColumnIndex(ContactsContract.Contacts._ID);
                String contactId = c.getString(idIndex);
                person = new Person(name, "", "");
                person.id = contactId;
                loadPersonData(person, contactId);

            }
            c.close();
        }
        return person;
    }

    public String getRawContactId(String contactId) {
        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
        String[] selectionArgs = new String[]{contactId};
        String rowId = "-1";
        Cursor c = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            rowId = c.getString(c.getColumnIndex(ContactsContract.RawContacts._ID));
            //Log.d("raww", "Contact Id: " + contactId + " Raw Contact Id: " + rowId);

        }
        c.close();
        return rowId;
    }

    public void matchContactNumbers(Person person, String id) {
        // Get numbers
        final String[] numberProjection = new String[]{
                Phone._ID,
                Phone.NUMBER,
                Phone.TYPE,
                Phone.LABEL,
                Phone.CONTACT_ID,
                Phone.PHOTO_URI
        };
        String where = Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{id, Phone.CONTENT_ITEM_TYPE};
        Cursor phone = context.getContentResolver().query(Data.CONTENT_URI, numberProjection, where, whereParameters, null);

        if (phone != null) {
            final int contactNumberColumnIndex = phone.getColumnIndex(Phone.NUMBER);
            final int contactTypeColumnIndex = phone.getColumnIndex(Phone.TYPE);
            final int phoneIdIndex = phone.getColumnIndex(Phone._ID);
            //final int contactIdColumnIndex = phone.getColumnIndex(Phone.CONTACT_ID);
            //final int contactPhotoIndex=phone.getColumnIndex(Phone.PHOTO_URI);
            //final int contactPhoneLabel=phone.getColumnIndex(Phone.LABEL);
            //Uri photo = ContentUris.withAppendedId( ContactsContract.Contacts.CONTENT_URI, Integer.valueOf(id));
            //photo = Uri.withAppendedPath( photo, ContactsContract.Contacts.Photo.PHOTO_URI );
            if (phone.moveToFirst()) {
                while (!phone.isAfterLast()) {
                    final String number = phone.getString(contactNumberColumnIndex);
                    final long phoneId = phone.getLong(phoneIdIndex);
                    //final String contactId = phone.getString(contactIdColumnIndex);
                    //final String image = phone.getString(contactPhotoIndex);
                    final int type = phone.getInt(contactTypeColumnIndex);

                    if (type == Phone.TYPE_HOME) {
                        person.homePhone = number;
                        person.homeId = phoneId;
                    } else if (type == Phone.TYPE_MOBILE) {
                        person.phone = number;
                        person.mobileId = phoneId;
                        person.phoneLabel = String.valueOf(type);
                        //person.icon = image;
                    }


                    phone.moveToNext();
                }

            }
            phone.close();
        }

    }

    public void matchPhoto(Person person, String id) {
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{id,
                ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE};
        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
        if (cursor.moveToFirst()) {

            //String path=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_FILE_ID))
        }

    }

    public void matchContactEmails(Person person, String id) {
        // Get email
        final String[] emailProjection = new String[]{
                Email.DATA,
                Email.TYPE
                //Email.LABEL,
                //Email.CONTACT_ID,
        };
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{id,
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
        Cursor email = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, emailProjection, where, whereParameters, null);

        if (email != null) {
            if (email.moveToFirst()) {
                final int contactEmailColumnIndex = email.getColumnIndex(Email.DATA);
                final int contactTypeColumnIndex = email.getColumnIndex(Email.TYPE);
                final String address = email.getString(contactEmailColumnIndex);
                final int type = email.getInt(contactTypeColumnIndex);
                person.email = address;
                person.emailLabel = String.valueOf(type);

            }
            email.close();
        }
        /*Cursor email = new CursorLoader(context,
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                emailProjection,
                null,
                null,
                null).loadInBackground();

        if (email.moveToFirst()) {
            final int contactEmailColumnIndex = email.getColumnIndex(Email.DATA);
            final int contactTypeColumnIndex = email.getColumnIndex(Email.TYPE);
            final int contactIdColumnsIndex = email.getColumnIndex(Email.CONTACT_ID);
            final int contactEmailLaeblIndex=email.getColumnIndex(Email.LABEL);

            while (!email.isAfterLast()) {
                final String address = email.getString(contactEmailColumnIndex);
                final String contactId = email.getString(contactIdColumnsIndex);
                final int type = email.getInt(contactTypeColumnIndex);
                String customLabel = email.getString(contactEmailLaeblIndex);
                Person person = contactsMap.get(contactId);
                if (person == null) {
                    continue;
                }
                if(person.email==null||person.email.equals("")) {
                    CharSequence emailType = ContactsContract.CommonDataKinds.Email.getTypeLabel(context.getResources(), type, customLabel);
                    person.email = address;
                    person.emailLabel = String.valueOf(type);
                }
                //Person.addEmail(address, emailType.toString());
                email.moveToNext();
            }
        }

        email.close();*/
    }

    public void matchOrg(Person person, String id) {
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{id,
                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};

        Cursor orgCur = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);

        if (orgCur != null) {
            if (orgCur.moveToFirst()) {
                String orgName = orgCur.getString(orgCur.getColumnIndex(Organization.COMPANY));
                String title = orgCur.getString(orgCur.getColumnIndex(Organization.TITLE));
                String department = orgCur.getString(orgCur.getColumnIndex(Organization.DEPARTMENT));
                person.company = orgName;
                person.department = department;
                person.title = title;
            }
            orgCur.close();
        }

    }

    public void matchNote(Person person, String id) {

        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{id,
                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
        Cursor noteCur = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
        if (noteCur != null) {
            if (noteCur.moveToFirst()) {
                String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                person.extra = note;
            }
            noteCur.close();
        }
    }

    public void matchAddress(Person person, String id) {
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{id,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};

        Cursor addrCur = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
        if (addrCur != null) {
            if (addrCur.moveToFirst()) {
                String poBox = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                String street = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                String city = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                String state = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                String postalCode = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                String country = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                String type = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                person.address = street;

            }
            addrCur.close();
        }
    }

    public void matchWebsite(Person person, String id) {
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{id,
                ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE};

        Cursor webCur = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);
        if (webCur != null) {
            if (webCur.moveToFirst()) {
                String website = webCur.getString(webCur.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                person.url = website;
            }

            webCur.close();
        }

    }

}
