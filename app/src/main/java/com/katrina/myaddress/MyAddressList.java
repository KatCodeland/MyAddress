package com.katrina.myaddress;

import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * Created by Ashley on 03/12/2017.
 */

public class MyAddressList extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>, OnDialogDoneListener{
    //CURSOR LOADER IS AN API THAT HELPS TO RETRIEVE DATA FROM DATABASE IN A THREAD SEPERATE TO THE UI THREAD TO AVOID "APPLICATION NOT RESPONDING ERROR"

    private SimpleCursorAdapter adapter;
    private static final int ACTIVITY_CREATE = 0; //StartactivityforResult receiver code for the the INSERT intent(from action menu)
    private static final int ACTIVITY_EDIT = 1; //StartactivityforResult receiver code for the the OnItemClick intent(from addressList)
    private static final int DELETE_ID = Menu.FIRST + 1;

    public static String ALERT_DIALOG_TAG = "ABOUT DIALOG";
    public static final String LOGTAG = "Katrina";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOGTAG, "onCreate() called");

       setContentView(R.layout.myaddress_list);
        this.getListView().setDividerHeight(2);
        fillData(); //method includes a call to get Loader Manager
       registerForContextMenu(getListView());
    }

    //ACTION BAR MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();

  inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.insert:
                //goes to the Address detail activity
               createAddress();
                return true;
            case R.id.about:
                this.AboutAlertDialog();
        }
        return super.onOptionsItemSelected(item);
    }


    //LIST MANAGEMENT
    // Opens the second activity if an entry is clicked
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, MyAddressDetailActivity.class);
        Uri addressUri = Uri.parse(MyAddressContentProvider.CONTENT_URI + "/" + id);
        i.putExtra(MyAddressContentProvider.CONTENT_ITEM_TYPE, addressUri);

        // Activity returns an result if called with startActivityForResult
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    // Called with the result of the other activity
    // requestCode was the origin request code send to the activity
    // resultCode is the return code, 0 is everything is ok
    // intend can be used to get data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }




    // Creates a new loader after the initLoader () call
    //Cursor loader is a subclass of AsyncTaskLoader so all data loader on a worker threat and not main thread
    //@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //when activity is created and there isn't already a loader instantiated then this method will get called
        String[] projection = { AddressTableHandler.COLUMN_ID, AddressTableHandler.COLUMN_FIRST_NAME, AddressTableHandler.COLUMN_LAST_NAME }; //to use to query the first and last name columns
        //sort order AddressTableHandler.COLUMN_FIRST_NAME + "COLLATE NOCASE ASC"

        //when we instantiate the cursor loader we give it the URI telling it which contact provider we want to access from, which in this case it for the Address table
        //
        CursorLoader cursorLoader = new CursorLoader(this, MyAddressContentProvider.CONTENT_URI, projection, null, null, null); //(maybe pass in a sort order paramenter A-Z
        return cursorLoader;
    }

    //@Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    //@Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }

   //DELETE GESTURE


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    //the menu that appears when you long press on row
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();  //get the info associated with this menu item
                Uri uri = Uri.parse(MyAddressContentProvider.CONTENT_URI + "/" + info.id); //concatenate the extracted id from the info Adapter Context Menu Info
                getContentResolver().delete(uri, null, null); //get the content provider and call the delete method
                fillData(); //update the list table
                return true;
        }
        return super.onContextItemSelected(item);
    }




    //CUSTOM METHODS
    private void createAddress() {
        //starts address detail activity
        Intent i = new Intent(this, MyAddressDetailActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    private void fillData() {
        //POPULATES THE UI TEXT FIELDS WITH THE SPECIFIED TEXT REFERENCES

        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[] {AddressTableHandler.COLUMN_FIRST_NAME };
        // Fields on the UI to which we map
        int[] to = new int[] { R.id.list_firstname };

        //initialize the loader and pass it 3 parameters id: uniquely identifies loader if you have multiple loaders
        //bundle which not using &, loader call back
        //this call will trigger the on createLoader method
        getLoaderManager().initLoader(0, null, this);

        //Set the Cursor adapter that will render data from cursor into the UI
        adapter = new SimpleCursorAdapter(this, R.layout.myaddress_row, null, from, to, 0);
        setListAdapter(adapter);
    }

    private void AboutAlertDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        AboutAlertDialogFragment adf = AboutAlertDialogFragment.newInstance("My AddressPlus is a nice and simple Android Application that allows a user to query, insert, update, delete, their home address. It is written for Android API 11 or higher. It supports tablets");

        adf.show(ft, ALERT_DIALOG_TAG);
    }
    public void onDialogDone(String tag, boolean cancelled, CharSequence message) {
        String s = tag + " responds with: " + message;

        if(cancelled) s = tag + " was cancelled by the user";

        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        Log.v(LOGTAG, s);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOGTAG, "onPaused() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOGTAG, "onStop() called");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOGTAG, "onDestroy() called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOGTAG, "onRestart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOGTAG, "onResume() called");
    }
}
