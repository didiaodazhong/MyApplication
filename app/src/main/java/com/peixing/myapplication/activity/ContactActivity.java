package com.peixing.myapplication.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.peixing.myapplication.R;
import com.peixing.myapplication.adapter.ContactAdapter;
import com.peixing.myapplication.bean.ContactInfo;
import com.peixing.myapplication.uikit.PermissionListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactActivity extends BaseappActivity {

    private static final String TAG = "ContactActivity";
    private String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};
    private RecyclerView recyclerContact;
    private ContentResolver contentResolver;
    private List<Map<String, String>> mp = new ArrayList<>();
    private ArrayList<ContactInfo> contactInfos;

    private ContactInfo contactInfo;
    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        contactInfos = new ArrayList<>();
        recyclerContact = (RecyclerView) findViewById(R.id.recycler_contact);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerContact.setLayoutManager(manager);
        recyclerContact.setHasFixedSize(true);
        //拿到内容访问者

        contactAdapter = new ContactAdapter(this, contactInfos);
        recyclerContact.setAdapter(contactAdapter);
//        getContact();
        contentResolver = getContentResolver();
        request();

    }

    /**
     * 获取通讯录数据
     */
    private void getContact() {

        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Cursor cs = contentResolver.query(uri, null, null, null, null, null);
        while (cs.moveToNext()) {
            //拿到联系人id 跟name
            int id = cs.getInt(cs.getColumnIndex("_id"));
            String name = cs.getString(cs.getColumnIndex("display_name"));
            //得到这个id的所有数据（data表）
            Uri uri1 = Uri.parse("content://com.android.contacts/raw_contacts/" + id + "/data");
            Cursor cs2 = contentResolver.query(uri1, null, null, null, null, null);
//            Map<String, String> maps = new HashMap<>();//实例化一个map

            contactInfo = new ContactInfo();
            while (cs2.moveToNext()) {

                //得到data这一列 ，包括很多字段
                String data1 = cs2.getString(cs2.getColumnIndex("data1"));
                //得到data中的类型
                String type = cs2.getString(cs2.getColumnIndex("mimetype"));
                String str = type.substring(type.indexOf("/") + 1, type.length());//截取得到最后的类型
                if ("name".equals(str)) {//匹配是否为联系人名字
//                    maps.put("name", data1);
                    contactInfo.setContactName(data1);
                }
                if ("phone_v2".equals(str)) {//匹配是否为电话
//                    maps.put("phone", data1);
                    contactInfo.setContactNumber(data1);
                }
                Log.i("test", data1 + "       " + type);
            }
            contactInfos.add(contactInfo);
//            mp.add(maps);//将map加入list集合中
        }
        contactAdapter.setContactInfos(contactInfos);
    }

    private void request() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestRunPermisssion(permissions, new PermissionListener() {
                @Override
                public void onGranted() {
                    Log.i("MainActivity", "onGranted: 授权成功");
//                    getPhoneContacts();
                    getContact();
                }

                @Override
                public void onDenied(List<String> deniedPermission) {
                    for (int i = 0; i < deniedPermission.size(); i++) {
                        Log.i("MainActivity", "onDenied: --权限--" + deniedPermission.get(i) + "--被拒绝了--");
                    }
                }
            });
        }
    }

    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 头像ID
     **/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**
     * 联系人的ID
     **/
    private static final int PHONES_CONTACT_ID_INDEX = 3;


    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};

    private void getPhoneContacts() {
//        ContentResolver resolver = mContext.getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;

                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                //得到联系人ID
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

                //得到联系人头像ID
                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

                //得到联系人头像Bitamp
                Bitmap contactPhoto = null;

                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                if (photoid > 0) {
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                } else {
                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delivery);
                }

//                mContactsName.add(contactName);
//                mContactsNumber.add(phoneNumber);
//                mContactsPhonto.add(contactPhoto);
                contactInfo = new ContactInfo();
                contactInfo.setContactId(contactid);
                contactInfo.setContactName(contactName);
                contactInfo.setContactNumber(phoneNumber);
                contactInfo.setPhoto(contactPhoto);
            }
            contactInfos.add(contactInfo);
        }
        phoneCursor.close();
        contactAdapter.setContactInfos(contactInfos);
    }

}
