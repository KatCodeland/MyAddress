package com.katrina.myaddress;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ashley on 30/11/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    //DEFAULT CONSTRUCTOR
    private static final String DATABASE_NAME = "myaddressplustable.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        AddressTableHandler.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        AddressTableHandler.onUpgrade(database, oldVersion, newVersion);
    }

}
