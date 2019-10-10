package com.garanti.test.customerproject.model;

public class Phone {

    private String customerNumber;
    private String phoneType;
    private short countryCode;
    private short areaCode;
    private long phoneNumber;

    public Phone(String customerNo, String type) {
        this.customerNumber = customerNo;
        this.phoneType = type;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }
}
