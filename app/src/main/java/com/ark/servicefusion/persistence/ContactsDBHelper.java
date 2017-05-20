package com.ark.servicefusion.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ark.servicefusion.model.Contact;
import com.ark.servicefusion.persistence.DatabaseContract.ContactEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akshayraj on 5/16/17.
 */


public class ContactsDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "ContactsDBHelper";
    // If you change the database schema, you must increment the database version.
    public static final int DB_VERSION = 1;
    public static final String SERVICEFUSION_DB_NAME = "ServiceFusion.db";

    private static final String SQL_CREATE_CONTACTS_TABLE =
            "CREATE TABLE " + DatabaseContract.ContactEntry.TABLE_CONTACTS + " (" +
                    ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ContactEntry.COLUMN_FIRSTNAME + " TEXT, " +
                    ContactEntry.COLUMN_LASTNAME + " TEXT, " +
                    ContactEntry.COLUMN_DATEOFBIRTH + " TEXT, " +
                    ContactEntry.COLUMN_PHONENUMBER + " TEXT, " +
                    ContactEntry.COLUMN_ZIPCODE + " TEXT " +
                    ");";

    //used in querying db
    public static final String PROJECTION[] = {
            ContactEntry._ID,
            ContactEntry.COLUMN_FIRSTNAME,
            ContactEntry.COLUMN_LASTNAME,
            ContactEntry.COLUMN_DATEOFBIRTH,
            ContactEntry.COLUMN_PHONENUMBER,
            ContactEntry.COLUMN_ZIPCODE
    };

    private static ContactsDBHelper sInstance;

    public static ContactsDBHelper getInstance(Context context){
        if(sInstance == null){
            sInstance = new ContactsDBHelper(context);
        }
        return sInstance;
    }

    private ContactsDBHelper(Context context) {
        super(context, SERVICEFUSION_DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //upgrade when DB_VERSION increments.
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public long insertContact(String firstName, String lastName, String dateOfBirth,
                              String phoneNumber, String zipCode) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactEntry.COLUMN_FIRSTNAME, firstName);
        contentValues.put(ContactEntry.COLUMN_LASTNAME, lastName);
        contentValues.put(ContactEntry.COLUMN_DATEOFBIRTH, dateOfBirth);
        contentValues.put(ContactEntry.COLUMN_PHONENUMBER, phoneNumber);
        contentValues.put(ContactEntry.COLUMN_ZIPCODE, zipCode);
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(ContactEntry.TABLE_CONTACTS, null, contentValues);
    }

    public void deleteContact(long rowId){
        Log.d(TAG, "deleteContact with _ID: " + rowId);

        SQLiteDatabase db = getWritableDatabase();
        String selection = ContactEntry._ID + "=?";
        String[] selectionArgs = { String.valueOf(rowId) };
        db.delete(ContactEntry.TABLE_CONTACTS, selection, selectionArgs);
    }

    public Cursor getContact(String firstName) throws SQLException {
        SQLiteDatabase db = getReadableDatabase();
        // Filter results WHERE "title" = 'My Title'
        String selection = ContactEntry.COLUMN_FIRSTNAME + " = ?";
        String[] selectionArgs = { firstName };
        //ORDER BY
        String orderBy = ContactEntry.COLUMN_LASTNAME + " DESC";
        Cursor result = db.query(ContactEntry.TABLE_CONTACTS, PROJECTION,
                selection, selectionArgs, null, null, orderBy,  null);
        if ((result.getCount() == 0) || !result.isFirst()) {
            throw new SQLException("No contact matching name: " + firstName);
        }
        return result;
    }

    public ArrayList<Contact> getAllContacts(){
        SQLiteDatabase db = getReadableDatabase();
        String groupBy = ContactEntry.COLUMN_LASTNAME + " ," + ContactEntry.COLUMN_FIRSTNAME;
        String orderBy = ContactEntry.COLUMN_FIRSTNAME + " ASC";
        Cursor cursor = db.query(ContactEntry.TABLE_CONTACTS, PROJECTION,
                null, null, groupBy, null, orderBy);

        ArrayList<Contact> contactList = new ArrayList<>();
        cursor.moveToFirst();
        int _idColumnIndex = cursor.getColumnIndex(ContactEntry._ID);
        int firstNameColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_FIRSTNAME);
        int lastNameColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_LASTNAME);
        int dobColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_DATEOFBIRTH);
        int phoneColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_PHONENUMBER);
        int zipColumnIndex = cursor.getColumnIndex(ContactEntry.COLUMN_ZIPCODE);
        while(!cursor.isAfterLast()) {
            Contact contact = new Contact(cursor.getString(firstNameColumnIndex),
                    cursor.getString(lastNameColumnIndex),
                    cursor.getString(dobColumnIndex),
                    cursor.getString(phoneColumnIndex),
                    cursor.getString(zipColumnIndex));
            contact.setId(cursor.getLong(_idColumnIndex));
            contactList.add(contact); //add the item
            cursor.moveToNext();
        }
        return contactList;
    }

    public boolean updateContact(long rowId, String firstName, String lastName, String dateOfBirth,
                                 String phoneNumber, String zipCode){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactEntry._ID, rowId);
        contentValues.put(ContactEntry.COLUMN_FIRSTNAME, firstName);
        contentValues.put(ContactEntry.COLUMN_LASTNAME, lastName);
        contentValues.put(ContactEntry.COLUMN_DATEOFBIRTH, dateOfBirth);
        contentValues.put(ContactEntry.COLUMN_PHONENUMBER, phoneNumber);
        contentValues.put(ContactEntry.COLUMN_ZIPCODE, zipCode);
        String selection = ContactEntry._ID +"=?";
        String[] selectionArgs = {String.valueOf(rowId)};
        return db.update(ContactEntry.TABLE_CONTACTS, contentValues, selection,selectionArgs) > 0;
    }

    public long insertContact(Contact contact) {
        return insertContact(contact.getFirstName(), contact.getLastName(), contact.getDateOfBirth(),
                contact.getPhoneNumber(), contact.getZipCode());
    }

    public boolean updateContact(Contact contact) {
        return updateContact(contact.getId(), contact.getFirstName(), contact.getLastName(),
                contact.getDateOfBirth(), contact.getPhoneNumber(), contact.getZipCode());
    }
}

