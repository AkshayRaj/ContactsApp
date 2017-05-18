package com.ark.servicefusion.model;

/**
 * Created by Lincoln on 15/01/16.
 */
public class Contact {
    private long _id = -1;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String dateOfBirth;
    private String zipCode;
    //for storing checkbox status in ContactsAdapter
    private boolean selected;

    public Contact() {
    }

    public Contact(String title, String lastName, String phoneNumber, String dateOfBirth, String zipCode) {
        this.firstName = title;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.zipCode = zipCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setId(long id) {
        this._id = id;
    }

    public long getId() {
        return _id;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected(){
        return selected;
    }
}
