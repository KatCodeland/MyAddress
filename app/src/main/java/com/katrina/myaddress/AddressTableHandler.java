package com.katrina.myaddress;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Ashley on 30/11/2017.
 */

public class AddressTableHandler {

    //DB table constants
    public static final String TABLE_ADDRESS = "addresses";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_FIRST_NAME = "firstName";
    public static final String COLUMN_LAST_NAME = "lastName";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_PROVINCE = "province";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_POSTAL_CODE = "postalCode";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ADDRESS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_FIRST_NAME + " text not null, "
            + COLUMN_LAST_NAME + " text not null, "
            + COLUMN_ADDRESS + " text not null, "
            + COLUMN_PROVINCE + " text not null, "
            + COLUMN_COUNTRY + " text not null, "
            + COLUMN_POSTAL_CODE + " text not null"
            + ");";

    //CREATE & UPGRADE METHODS
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(AddressTableHandler.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS);
        onCreate(database);
    }
}
