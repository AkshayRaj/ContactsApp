package com.ark.servicefusion.persistence;

import android.provider.BaseColumns;

/**
 ** A contract class is a container for constants that define names for URIs, tables, and columns.
 * The contract class allows you to use the same constants across all the other classes in the same package.
 * This lets you change a column name in one place and have it propagate throughout your code.
 *
 * A good way to organize a contract class is to put definitions that are global to your whole database in the root level of the class.
 * Then create an inner class for each table that enumerates its columns.
 */
public final class DatabaseContract {

    private DatabaseContract(){

    }

    /**
     * By implementing the BaseColumns interface, your inner class can inherit a primary key field called _ID,
     * that some Android classes such as cursor adaptors will expect it to have.
     * It's not required, but this can help your database work harmoniously with the Android framework.
     */
    public static class ContactEntry implements BaseColumns{

        public static final String TABLE_CONTACTS = "contacts";
        public static final String COLUMN_FIRSTNAME = "firstname";
        public static final String COLUMN_LASTNAME = "lastname";
        public static final String COLUMN_DATEOFBIRTH = "dateofbirth";
        public static final String COLUMN_PHONENUMBER = "phonenumber";
        public static final String COLUMN_ZIPCODE = "zipcode";
    }
}
