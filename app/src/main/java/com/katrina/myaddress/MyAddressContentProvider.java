package com.katrina.myaddress;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Ashley on 30/11/2017.
 */

public class MyAddressContentProvider extends ContentProvider {

    // database
    private DatabaseHandler database;

    // Used for the UriMacher
    private static final int ADDRESSES = 10;
    private static final int ADDRESS_ID = 20;

    private static final String AUTHORITY = "com.katrina.myaddress";
    private static final String BASE_PATH = "addresses";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/addresses";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/address";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH); //create instance of class

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, ADDRESSES); //add to object uri paths for entire table
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ADDRESS_ID); //add to object uri paths for specified row in table
    }

    @Override
    public boolean onCreate() {
        database = new DatabaseHandler(getContext());
        return false;
    }

    @Nullable
    @Override
    //query by ID
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //A BUNCH OF "DEFENSIVE PROGRAMMING" ACTIONS BEFORE PERFORMING QUERY ON DB
        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder(); // a class that helps build SQL queries

        // Check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(AddressTableHandler.TABLE_ADDRESS); //sets the list of table to query

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case ADDRESSES:
                break;
            case ADDRESS_ID:
                // Adding the ID to the original query
                //method appends a chunk to the WHERE clause of the query, the WHERE clause is a condition
                queryBuilder.appendWhere(AddressTableHandler.COLUMN_ID + "=" + uri.getLastPathSegment());//gets the last part of the uri path
                //eg. produces SQL statement "WHERE _id = x"
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        //NOW WERE ALL GOOD TO GO TO SEND QUERY TO DB
        SQLiteDatabase db = database.getWritableDatabase(); //connection to DB (should only need Readable here?????)
        //use builder to build the query request using the arguements passed into this query override method
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        //projection, A list of which columns to return , passing null will return all columns
//        selection, A filter declaring which rows to return
//        selectionArgs,
        //groupby,
//        String having,
//        String sortOrder

        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
        //cursors will contain teh result set of the data that fits query criteria
    }

    @Nullable
    @Override
    //not using
    public String getType(@NonNull Uri uri) {

        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
            int uriType = sURIMatcher.match(uri); //will return a code of value 10, 20 or -1? no match
            SQLiteDatabase sqlDB = database.getWritableDatabase(); //get writeable connection to database

            long id = 0; //hold the result of the insert method //make variable outside of method so that it can be returned
            //pass in the returned code from the uri matching
            switch (uriType) {
                case ADDRESSES: // code 10
                    //call insert method from DB object and pass in address DB from ATDB handler class, along with the content values that was pass into this method
                    //once complete the method will return a value to indicate whether the row was inserted or not 1 or -1 or row number???
                    id = sqlDB.insert(AddressTableHandler.TABLE_ADDRESS, null, values);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            //retrieves the context this provider is running in, gets the content resolver for this db,
            getContext().getContentResolver().notifyChange(uri, null);
            return Uri.parse(BASE_PATH + "/" + id); //returns a string with the row number
        //this method returns a URI telling the caller exactly where the data was inserted
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case ADDRESSES:
                //deletes the whole table
                rowsDeleted = sqlDB.delete(AddressTableHandler.TABLE_ADDRESS, selection, selectionArgs);
                break;
            case ADDRESS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) { //if no rows were specified
                    rowsDeleted = sqlDB.delete(AddressTableHandler.TABLE_ADDRESS, AddressTableHandler.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(AddressTableHandler.TABLE_ADDRESS, AddressTableHandler.COLUMN_ID + "=" + id
                            + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
        //this method returns an integer telling the caller how many rows were deleted
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;

        switch (uriType) {
            case ADDRESSES:
                rowsUpdated = sqlDB.update(AddressTableHandler.TABLE_ADDRESS, values, selection, selectionArgs);
                break;
            case ADDRESS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(AddressTableHandler.TABLE_ADDRESS, values,
                            AddressTableHandler.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(AddressTableHandler.TABLE_ADDRESS,
                            values, AddressTableHandler.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
        //this method returns an integer telling the caller how many rows were updated
    }

    private void checkColumns(String[] projection) {
        //these are the table column header names we have from our TableHandler which we will compare the projection against
        String[] available = { AddressTableHandler.COLUMN_ID,
                AddressTableHandler.COLUMN_TITLE, AddressTableHandler.COLUMN_FIRST_NAME,
                AddressTableHandler.COLUMN_LAST_NAME, AddressTableHandler.COLUMN_ADDRESS, AddressTableHandler.COLUMN_PROVINCE, AddressTableHandler.COLUMN_PROVINCE,
                AddressTableHandler.COLUMN_COUNTRY, AddressTableHandler.COLUMN_POSTAL_CODE};

        if (projection != null) {
            //hashset is a Java collection
            //convert both arrays into a HASHset to prepare for comparisons
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
