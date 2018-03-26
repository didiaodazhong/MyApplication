package com.peixing.myapplication.bean;

import android.graphics.Bitmap;

/**
 * Created by xuxiaohe on 2018/1/5.
 */

public class ContactInfo {
    private String contactName;
    private String contactNumber;
    private String contactSortKey;
    private Long contactId;
    private Bitmap photo;

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactSortKey() {
        return contactSortKey;
    }

    public void setContactSortKey(String contactSortKey) {
        this.contactSortKey = contactSortKey;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }
}
