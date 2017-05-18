package com.ark.servicefusion.ui.recyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ark.servicefusion.R;
import com.ark.servicefusion.model.Contact;

import java.util.List;


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private static final String TAG = "ContactsAdapter";
    private int VISIBILITY = View.INVISIBLE;
    private List<Contact> contactList;

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public CheckBox checkBox;//invisible by default, visible for selecting contact for deletion


        public ContactViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.textview_contactname);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox_contact);
        }
    }

    public ContactsAdapter(List<Contact> contactList) {
        this.contactList = contactList;
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_contact, parent, false);

        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder holder, final int position) {
        final Contact contact = contactList.get(position);
        holder.name.setText(contact.getFirstName() + " " + contact.getLastName());
        holder.checkBox.setVisibility(VISIBILITY);
        holder.checkBox.setChecked(contact.isSelected());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, contact.getFirstName() + " : " + contact.isSelected());
                contact.setSelected(isChecked);
                Log.d(TAG, contact.getFirstName() + " : " + contact.isSelected());
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void setContactList(List<Contact> contactList){
        this.contactList = contactList;
        notifyDataSetChanged();
    }

    public List<Contact> getContactList(){
        return contactList;
    }

    public void removeItem(int index){
        contactList.remove(index);
        notifyDataSetChanged();
    }

    public void removeItems(List<Contact> contactsToDelete){
        contactList.removeAll(contactsToDelete);
        notifyDataSetChanged();
    }

    public void setCheckboxVisibility(int visibiltiy){
        VISIBILITY = visibiltiy;
        notifyDataSetChanged();
    }
}
