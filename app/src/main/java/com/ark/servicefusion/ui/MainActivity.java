package com.ark.servicefusion.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ark.servicefusion.R;
import com.ark.servicefusion.model.Contact;
import com.ark.servicefusion.persistence.ContactsDBHelper;
import com.ark.servicefusion.persistence.DatabaseContract.ContactEntry;
import com.ark.servicefusion.ui.recyclerview.*;
import com.ark.servicefusion.ui.recyclerview.adapter.ContactsAdapter;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    protected static final int CREATE_CONTACT_CODE = 101;
    public static final String DATE_FORMAT = "MM-dd-yyyy";

    private boolean mIsCheckboxVisible = false;

    private List<Contact> mContactList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactsAdapter mAdapter;
    private FloatingActionButton addContactButton;
    Toolbar toolbar;
    MenuItem deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ContactsAdapter(mContactList);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Contact contact = mContactList.get(position);
                Toast.makeText(getApplicationContext(), contact.getFirstName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                //show exit and delete button at top,
                flipVisibility();
            }
        }));

        addContactButton = (FloatingActionButton) findViewById(R.id.button_addcontact);
        addContactButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent saveContactIntent = new Intent(getApplicationContext(), AddContactActivity.class);
                startActivityForResult(saveContactIntent, CREATE_CONTACT_CODE);
            }
        });

        prepareContacts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        deleteButton = menu.findItem(R.id.button_delete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_delete:
                deleteContacts();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult, requestCode: " + requestCode + " resultCode: " + resultCode);
        if(data != null){
            Contact contact = new Contact(data.getStringExtra(ContactEntry.COLUMN_FIRSTNAME),
                    data.getStringExtra(ContactEntry.COLUMN_LASTNAME),
                    data.getStringExtra(ContactEntry.COLUMN_DATEOFBIRTH),
                    data.getStringExtra(ContactEntry.COLUMN_PHONENUMBER),
                    data.getStringExtra(ContactEntry.COLUMN_ZIPCODE));
            contact.setId(data.getLongExtra(ContactEntry._ID, -1));
            mContactList.add(contact);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void deleteContacts(){
        Log.d(TAG, "deleteContacts()");
        ArrayList<Contact> contactsToDelete = new ArrayList<>();
        List<Integer> deletedContactsIndices = new ArrayList<>();
        for(int index = 0 ; index < mAdapter.getContactList().size(); index++){
            if(mAdapter.getContactList().get(index).isSelected()){
                Contact contact = mAdapter.getContactList().get(index);
                contactsToDelete.add(contact);
                deletedContactsIndices.add(index);
            }
        }
        final ArrayList<Contact> contactArrayList = contactsToDelete;
        new Thread() {
            @Override
            public void run() {
                Log.d(TAG, "Running deleteContactsThread: " + contactArrayList.size());
                try {
                    for (Contact contact : contactArrayList) {
                        ContactsDBHelper.getInstance(getApplicationContext()).deleteContact(contact.getId());
                    }
                } catch (Exception exception){
                    Log.e(TAG, "Exception occurred while deleting contacts in deleteContactsThread", exception);
                }
            }
        }.start();

        mAdapter.removeItems(contactsToDelete);
        resetCheckBoxes();
    }

    private void flipVisibility(){
        Log.d(TAG, "flipVisibility()");

        if(mIsCheckboxVisible) {
            mAdapter.setCheckboxVisibility(View.INVISIBLE);
        } else {
            mAdapter.setCheckboxVisibility(View.VISIBLE);
        }

        deleteButton.setVisible(!mIsCheckboxVisible);
        mIsCheckboxVisible = !mIsCheckboxVisible;
    }

    private void resetCheckBoxes() {
        mAdapter.setCheckboxVisibility(View.INVISIBLE);
    }

    private void prepareContacts() {

        ArrayList<Contact> contactsToInsert = new ArrayList<>();
        Contact contact = new Contact("Bruce", "Wayne", "May-27-1939", "911", "53540");
        contactsToInsert.add(contact);
        contact = new Contact("Clark", "Kent", "June-18-1960", "911", "67524");
        contactsToInsert.add(contact);
        contact = new Contact("Tony", "Stark", "June-18-1960", "911", "67524");
        contactsToInsert.add(contact);
        contact = new Contact("Barry", "Allen", "June-18-1960", "911", "67524");
        contactsToInsert.add(contact);
        contact = new Contact("Steve", "Rogers", "June-18-1960", "911", "67524");
        contactsToInsert.add(contact);
        contact = new Contact("Bruce", "Banner", "June-18-1960", "911", "67524");
        contactsToInsert.add(contact);
        contact = new Contact("Thor", "Odinson", "June-18-1960", "911", "67524");
        contactsToInsert.add(contact);

        for(Contact insertContact: contactsToInsert){
            ContactsDBHelper.getInstance(getApplicationContext()).insertContact(insertContact);
        }

        mContactList.addAll(ContactsDBHelper.getInstance(getApplicationContext())
                .getAllContacts());
        mAdapter.notifyDataSetChanged();

//

    }

}
