package com.ark.servicefusion.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ark.servicefusion.R;
import com.ark.servicefusion.model.Contact;
import com.ark.servicefusion.persistence.ContactsDBHelper;
import com.ark.servicefusion.persistence.DatabaseContract.ContactEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Akshayraj on 5/18/17.
 */

public class ContactDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ContactDetailsActivity";
    private long _id;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String phoneNumber;
    private String zipCode;
    boolean isUpdated = false;

    private EditText dobEditText;
    private EditText phoneEditText;
    private EditText zipEditText;

    private Toolbar toolbar;
    private ImageButton exitButton;
    private TextView ContactName;
    private MenuItem editButton;
    private MenuItem saveButton;
    private boolean mIsEditMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactdetails);
        toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        exitButton = (ImageButton) findViewById(R.id.button_exitcontactdetails);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isUpdated){
                    setResult(RESULT_CANCELED);
                }
                if(mIsEditMode){
                    editMode(false);
                } else {
                    finish();
                }
            }
        });

        _id = getIntent().getLongExtra(ContactEntry._ID, -1);
        firstName = getIntent().getStringExtra(ContactEntry.COLUMN_FIRSTNAME);
        lastName = getIntent().getStringExtra(ContactEntry.COLUMN_LASTNAME);
        dateOfBirth = getIntent().getStringExtra(ContactEntry.COLUMN_DATEOFBIRTH);
        phoneNumber = getIntent().getStringExtra(ContactEntry.COLUMN_PHONENUMBER);
        zipCode = getIntent().getStringExtra(ContactEntry.COLUMN_ZIPCODE);

        ContactName = (TextView) findViewById(R.id.title_contactdetails);
        ContactName.setText(firstName + " " + lastName);
        dobEditText = (EditText) findViewById(R.id.tv_dob);
        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "dobEditText onClick()");
                if(mIsEditMode) {
                    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MainActivity.DATE_FORMAT, Locale.US);
                    Date dobDate = new Date(System.currentTimeMillis());//initialize useful; if try/catch throws exception
                    try {
                        dobDate = simpleDateFormat.parse(dobEditText.getText().toString());
                    } catch (ParseException e) {
                        //catch and continue
                        Log.d(TAG, "Error parsing date.", e);
                    }
                    final Calendar dobCalendar = Calendar.getInstance();
                    dobCalendar.setTime(dobDate);
                    new DatePickerDialog(ContactDetailsActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    dobCalendar.set(year, monthOfYear, dayOfMonth);
                                        dobEditText.setText(simpleDateFormat.format(dobCalendar.getTime()));
                                }
                            },
                            dobCalendar.get(Calendar.YEAR),
                            dobCalendar.get(Calendar.MONTH),
                            dobCalendar.get(Calendar.DAY_OF_MONTH))
                            .show();
                }
            }
        });
        dobEditText.setText(dateOfBirth);
        phoneEditText = (EditText) findViewById(R.id.tv_phonenumber);
        phoneEditText.setText(phoneNumber);
        zipEditText = (EditText) findViewById(R.id.tv_zipcode);
        zipEditText.setText(zipCode);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contactdetails, menu);

        editButton = menu.findItem(R.id.item_editcontact);
        editButton.setVisible(true);
        saveButton = menu.findItem(R.id.item_savecontact);
        saveButton.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_editcontact:
                editMode(true);
                return true;
            case R.id.item_savecontact:
                saveContact();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveContact() {
        phoneNumber = phoneEditText.getText().toString();
        dateOfBirth = dobEditText.getText().toString();
        zipCode = zipEditText.getText().toString();
        Contact contact = new Contact(firstName, lastName, dateOfBirth, phoneNumber, zipCode);
        contact.setId(_id);
        isUpdated = ContactsDBHelper.getInstance(getApplicationContext())
                .updateContact(contact);
        Intent contactData = new Intent();
        contactData.putExtra(ContactEntry._ID, _id);
        contactData.putExtra(ContactEntry.COLUMN_FIRSTNAME, contact.getFirstName());
        contactData.putExtra(ContactEntry.COLUMN_LASTNAME, contact.getLastName());
        contactData.putExtra(ContactEntry.COLUMN_DATEOFBIRTH, contact.getDateOfBirth());
        contactData.putExtra(ContactEntry.COLUMN_PHONENUMBER, contact.getPhoneNumber());
        contactData.putExtra(ContactEntry.COLUMN_ZIPCODE, contact.getZipCode());

        setResult(MainActivity.RESULT_CODE_CONTACTUPDATED, contactData);

        dobEditText.setText(contact.getDateOfBirth());
        phoneEditText.setText(contact.getPhoneNumber());
        zipEditText.setText(contact.getZipCode());

        editMode(false);
    }

    private void editMode(boolean isEditMode) {
        mIsEditMode = isEditMode;

        if(mIsEditMode) {
            toolbar.setBackgroundColor(Color.LTGRAY);
            exitButton.setBackgroundColor(Color.LTGRAY);
            editButton.setVisible(false);
            saveButton.setVisible(true);
            dobEditText.setEnabled(true); dobEditText.setFocusable(false);dobEditText.setClickable(true);
            phoneEditText.setEnabled(true);
            zipEditText.setEnabled(true);
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            exitButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            editButton.setVisible(true);
            saveButton.setVisible(false);
            dobEditText.setEnabled(false); dobEditText.setFocusable(false); dobEditText.setClickable(false);
            phoneEditText.setEnabled(false);
            zipEditText.setEnabled(false);
        }
    }
}
