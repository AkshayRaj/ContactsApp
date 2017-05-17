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

    private List<Contact> contactList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactsAdapter mAdapter;
    private FloatingActionButton addContactButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ContactsAdapter(contactList);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Contact contact = contactList.get(position);
                Toast.makeText(getApplicationContext(), contact.getFirstName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                //show exit and delete button at top,
                //show checkbox selection
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult, requestCode: " + requestCode + " resultCode: " + resultCode);
        Contact contact = new Contact(data.getStringExtra(ContactEntry.COLUMN_FIRSTNAME),
                data.getStringExtra(ContactEntry.COLUMN_LASTNAME),
                data.getStringExtra(ContactEntry.COLUMN_DATEOFBIRTH),
                data.getStringExtra(ContactEntry.COLUMN_PHONENUMBER),
                data.getStringExtra(ContactEntry.COLUMN_ZIPCODE));
        contact.setId(data.getLongExtra(ContactEntry._ID, -1));
        contactList.add(contact);
        mAdapter.notifyDataSetChanged();
    }

    private void prepareContacts() {

        contactList.addAll(ContactsDBHelper.getInstance(getApplicationContext())
                .getAllContacts());

        mAdapter.notifyDataSetChanged();

//        Contact contact = new Contact("Bruce", "Wayne", "May-27-1939", "911", "53540");
//        contactList.add(contact);
//
//        contact = new Contact("Clark", "Kent", "June-18-1960", "911", "67524");
//        contactList.add(contact);
    }

}
