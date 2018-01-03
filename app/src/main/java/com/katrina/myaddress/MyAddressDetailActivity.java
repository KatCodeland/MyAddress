package com.katrina.myaddress;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class MyAddressDetailActivity extends Activity {

    private RadioGroup mDesignation;
    private RadioButton mMrRadio;
    private RadioButton mMrsRadio;
    private RadioButton mMsRadio;
    private RadioButton mDrRadio;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mAddress;
    private Spinner mProvince;
    private EditText mCountry;
    private EditText mPostalCode;
    private Uri addressUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDesignation = (RadioGroup) findViewById(R.id.designationButtons);
        mMrRadio = (RadioButton) findViewById(R.id.mr);
        mMrsRadio = (RadioButton) findViewById(R.id.mrs);
        mMsRadio = (RadioButton) findViewById(R.id.ms);
        mDrRadio = (RadioButton) findViewById(R.id.dr);
        mFirstName = (EditText) findViewById(R.id.editFirstName);
        mLastName = (EditText) findViewById(R.id.editSecondtName);
        mAddress = (EditText) findViewById(R.id.editAddress);
        mProvince = (Spinner) findViewById(R.id.provinceSelector);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.provinces, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        mProvince.setAdapter(adapter);
        mCountry = (EditText) findViewById(R.id.editCountry);
        mPostalCode = (EditText) findViewById(R.id.editPostalCode);
        Button submitButton = (Button) findViewById(R.id.submitButton);


        //CHECKING WHICH INTENT RECEIEVED , INSERT OR EDIT
        //THE INSERT INTENT HAS NO EXTRAS
        //THE EDIT HAS AN EXTRA WITH THE ADDRESS URI
        Bundle extras = getIntent().getExtras();
        // Check from the saved Instance
        //JAVA TERNARY OPERATER
        // variable x = (expression) ? value if true : value if false
        //So if there is an extra in the bundle get the value for the key (getparcelable(key) it to the URI variable names addressUri
        //if no extra set the URI to null
        addressUri = (savedInstanceState == null) ? null : (Uri) savedInstanceState.getParcelable(MyAddressContentProvider.CONTENT_ITEM_TYPE);


        // Or passed from the other activity
        //SO IF THE EXTRA IS NOT NULL - THEN THIS INTENT WAS FOR THE "EDIT"
        //HERE WE WANT TO CARRY TEXT INFORMATION FROM THE LIST ROW & USE THIS TO QUERY FROM THE DB
        if (extras != null) { //meaning we are going to edit an entry
            addressUri = extras.getParcelable(MyAddressContentProvider.CONTENT_ITEM_TYPE);
            fillData(addressUri);
        }


        //ACTION TO TAKE WHEN THE SUBMIT BUTTON PRESSED
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(mFirstName.getText().toString()) || TextUtils.isEmpty(mLastName.getText().toString()) || TextUtils.isEmpty(mAddress.getText().toString()) || TextUtils.isEmpty(mCountry.getText().toString())|| TextUtils.isEmpty(mPostalCode.getText().toString())) { //RETURNS TRUE if the string is null
                    makeToast();
                } else {
                    setResult(RESULT_OK); //to send back to parent activity
                    finish();
                }
            }

        });
    }

    private void fillData(Uri uri) {
        String[] projection = { AddressTableHandler.COLUMN_TITLE, AddressTableHandler.COLUMN_FIRST_NAME, AddressTableHandler.COLUMN_LAST_NAME, AddressTableHandler.COLUMN_ADDRESS,
                AddressTableHandler.COLUMN_PROVINCE, AddressTableHandler.COLUMN_COUNTRY, AddressTableHandler.COLUMN_POSTAL_CODE };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null); //use cursor to extract all rows for the specified column, but because we are querying with a Uri for one item, should only be one row in cursor
        //cursor will return data set of all contacts
        if (cursor != null) { //if there is data in the cursor
            cursor.moveToFirst(); //set the cursor to the first row, should only be one anyway
            String province = cursor.getString(cursor.getColumnIndexOrThrow(AddressTableHandler.COLUMN_PROVINCE)); //setting the spinner, so get data for the province column from the cursor

            //SETTING THE SPINNER FROM THE DATABASE
            //cycle through the string array for the spinner & if the string in array == the string returned from query set the spinner to that position
            for (int i = 0; i < mProvince.getCount(); i++) { //if less that the number of strings in the string array in resources
                String s = (String) mProvince.getItemAtPosition(i);
                if (s.equalsIgnoreCase(province)) { //compare two strings and ignore the CASE (caseinsensitive)
                    mProvince.setSelection(i);
                }
            }

            //SET THE RADIO BUTTONS FROM THE DATABASE
            //get text from data base
            String title = cursor.getString(cursor.getColumnIndexOrThrow(AddressTableHandler.COLUMN_TITLE));
            //get text from radio buttons into variables
            if (title.equalsIgnoreCase(mMrRadio.getText().toString())){
                mMrRadio.setChecked(true);
            }else if(title.equalsIgnoreCase(mMrsRadio.getText().toString())){
                mMrsRadio.setChecked(true);
            }else if(title.equalsIgnoreCase(mMsRadio.getText().toString())){
                mMsRadio.setChecked(true);
            }else if(title.equalsIgnoreCase(mDrRadio.getText().toString())) {
                mDrRadio.setChecked(true);
            }


            //set all the other textviews to the extracted cursor data
            mFirstName.setText(cursor.getString(cursor.getColumnIndexOrThrow(AddressTableHandler.COLUMN_FIRST_NAME)));
            mFirstName.setText(cursor.getString(cursor.getColumnIndexOrThrow(AddressTableHandler.COLUMN_FIRST_NAME)));
            mLastName.setText(cursor.getString(cursor.getColumnIndexOrThrow(AddressTableHandler.COLUMN_LAST_NAME)));
            mAddress.setText(cursor.getString(cursor.getColumnIndexOrThrow(AddressTableHandler.COLUMN_ADDRESS)));
            mCountry.setText(cursor.getString(cursor.getColumnIndexOrThrow(AddressTableHandler.COLUMN_COUNTRY)));
            mPostalCode.setText(cursor.getString(cursor.getColumnIndexOrThrow(AddressTableHandler.COLUMN_POSTAL_CODE)));

            // Always close the cursor
            cursor.close();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(MyAddressContentProvider.CONTENT_ITEM_TYPE, addressUri);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() { //what data we want saving when activity backgrounded or destroyed
        //also here we access the DB and update the row with the details

        String title =((RadioButton)findViewById(mDesignation.getCheckedRadioButtonId())).getText().toString();
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String address = mAddress.getText().toString();
        String province = (String) mProvince.getSelectedItem();
        String country = mCountry.getText().toString();
        String postalCode = mPostalCode.getText().toString();

        // Only save if every field has an entry
        if (title.length() == 0 && firstName.length() == 0 && lastName.length() == 0 && address.length() == 0 && province.length() == 0 && country.length() == 0 && postalCode.length() == 0) {
            return;
        }


        ContentValues values = new ContentValues();
        values.put(AddressTableHandler.COLUMN_TITLE, title);
        values.put(AddressTableHandler.COLUMN_FIRST_NAME, firstName);
        values.put(AddressTableHandler.COLUMN_LAST_NAME, lastName);
        values.put(AddressTableHandler.COLUMN_ADDRESS, address);
        values.put(AddressTableHandler.COLUMN_PROVINCE, province);
        values.put(AddressTableHandler.COLUMN_COUNTRY, country);
        values.put(AddressTableHandler.COLUMN_POSTAL_CODE, postalCode);


        if (addressUri == null) { //then we are insert a new entry into the database
            // New ToDo
            addressUri = getContentResolver().insert(MyAddressContentProvider.CONTENT_URI, values);
        } else { //we are updating an existing entry into the database
            // Update ToDo
            getContentResolver().update(addressUri, values, null, null);
        }
    }

    private void makeToast() {
        Toast.makeText(MyAddressDetailActivity.this, "Please complete all fields",Toast.LENGTH_LONG).show();
    }

}
