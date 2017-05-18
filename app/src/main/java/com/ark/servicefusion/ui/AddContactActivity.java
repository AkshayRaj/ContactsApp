package com.ark.servicefusion.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.ark.servicefusion.R;
import com.ark.servicefusion.model.Contact;
import com.ark.servicefusion.persistence.ContactsDBHelper;
import com.ark.servicefusion.persistence.DatabaseContract.ContactEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Akshayraj on 5/16/17.
 */

public class AddContactActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText dateOfBirth;
    private EditText phoneNumber;
    private EditText zipCode;
    private Button saveButton;
    Calendar myCalendar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newcontact);

        myCalendar = Calendar.getInstance();//init calendar with current time

        firstName = (EditText) findViewById(R.id.edittext_firstname);
        lastName = (EditText) findViewById(R.id.edittext_lastname);
        dateOfBirth = (EditText) findViewById(R.id.edittext_dob);
        dateOfBirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(AddContactActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MainActivity.DATE_FORMAT, Locale.US);
                        dateOfBirth.setText(simpleDateFormat.format(myCalendar.getTime()));
                    }
                },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        phoneNumber = (EditText) findViewById(R.id.edittext_phonenumber);
        zipCode = (EditText) findViewById(R.id.edittext_zipcode);

        saveButton = (Button) findViewById(R.id.button_savecontact);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact = new Contact(firstName.getText().toString(),
                        lastName.getText().toString(),
                        dateOfBirth.getText().toString(),
                        phoneNumber.getText().toString(),
                        zipCode.getText().toString());
                long _id = ContactsDBHelper.getInstance(getApplicationContext())
                        .insertContact(contact);
                contact.setId(_id);
                Intent contactData = new Intent();
                contactData.putExtra(ContactEntry._ID, contact.getId());
                contactData.putExtra(ContactEntry.COLUMN_FIRSTNAME, contact.getFirstName());
                contactData.putExtra(ContactEntry.COLUMN_LASTNAME, contact.getLastName());
                contactData.putExtra(ContactEntry.COLUMN_DATEOFBIRTH, contact.getDateOfBirth());
                contactData.putExtra(ContactEntry.COLUMN_PHONENUMBER, contact.getPhoneNumber());
                contactData.putExtra(ContactEntry.COLUMN_ZIPCODE, contact.getZipCode());

                setResult(RESULT_OK, contactData);
                finish();
            }
        });

    }

}
