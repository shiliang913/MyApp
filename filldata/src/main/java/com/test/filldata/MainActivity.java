package com.test.filldata;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvProgress;
    EditText etInput;
    Button btnContact, btnSMS, btnCall, btnCalendar;
    int count = 1, times = 0, savedTimes;
    long timer;
    final String TAG = "debug";
    Uri uri;
    ContentResolver contentResolver;
    ContentValues contentValues = new ContentValues();
    InputMethodManager inputMethodManager;
    final int CURRENT_TIMES = 1;
    final int FILL_CONTACTS_COMPLETED = 2;
    final int FILL_SMS_COMPLETED = 3;
    final int FILL_CALL_COMPLETED = 4;
    final int FILL_CALENDAR_COMPLETED = 5;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        tvProgress = (TextView) findViewById(R.id.tvProgress);
        etInput = (EditText) findViewById(R.id.etIpnut);
        btnContact = (Button) findViewById(R.id.btnContact);
        btnSMS = (Button) findViewById(R.id.btnSMS);
        btnCall = (Button) findViewById(R.id.btnCall);
        btnCalendar = (Button) findViewById(R.id.btnCalendar);
        contentResolver = getContentResolver();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CURRENT_TIMES:
                        tvProgress.setText(savedTimes + "");
                        break;
                    case FILL_CONTACTS_COMPLETED:
                        tvProgress.setText("Fill Contacts\nCompleted");
                        break;
                    case FILL_SMS_COMPLETED:
                        tvProgress.setText("Fill SMS\nCompleted");
                        break;
                    case FILL_CALL_COMPLETED:
                        tvProgress.setText("Fill Call Logs\nCompleted");
                        break;
                    case FILL_CALENDAR_COMPLETED:
                        tvProgress.setText("Fill Calendar Events\nCompleted");
                        break;
                }
            }
        };

        btnContact.setOnClickListener(this);
        btnSMS.setOnClickListener(this);
        btnCall.setOnClickListener(this);
        btnCalendar.setOnClickListener(this);
        Log.e(TAG, "初始化完成");

    }

    @Override
    public void onClick(final View view) {
        inputMethodManager.hideSoftInputFromWindow(etInput.getWindowToken(), 0);
        if (!TextUtils.isEmpty(etInput.getText())) {
            times = Integer.parseInt(etInput.getText().toString());
        }
        if ((times > 5000) || (times < 1)) {
            Toast.makeText(this, "请输入1至5000", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                switch (view.getId()) {
                    case R.id.btnContact:
                        fillContacts(times);
                        break;
                    case R.id.btnSMS:
                        fillSMS(times);
                        break;
                    case R.id.btnCall:
                        fillCall(times);
                        break;
                    case R.id.btnCalendar:
                        fillCalendar(times);
                        break;
                }
            }
        }.start();
    }

    public void fillCalendar(int num) {
        timer = System.currentTimeMillis();
        for (; count <= num; count++) {
            contentValues.clear();
            contentValues.put(CalendarContract.Events.TITLE, "calendar title" + count);
            contentValues.put(CalendarContract.Events.DESCRIPTION, "calendar description" + count);
            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, "GMT+8");
            contentValues.put(CalendarContract.Events.DTSTART, System.currentTimeMillis()+count*60000);
            contentValues.put(CalendarContract.Events.DTEND, System.currentTimeMillis()+(count+1)*60000);
            contentValues.put(CalendarContract.Events.CALENDAR_ID, 1);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues);
            if(count%10 == 0) {
                savedTimes = count;
                handler.sendEmptyMessage(CURRENT_TIMES);
            }
        }
        handler.sendEmptyMessage(FILL_CALENDAR_COMPLETED);
        count = 1;
        times = 0;
        timer = System.currentTimeMillis()-timer;
        Log.e(TAG,"Fill calendar events cost "+timer/1000+" seconds");
    }
    public void fillCall(int num) {
        timer = System.currentTimeMillis();
        for (; count <= num; count++) {
            contentValues.clear();
            contentValues.put(CallLog.Calls.TYPE, CallLog.Calls.INCOMING_TYPE);
            contentValues.put(CallLog.Calls.NUMBER, "12345678" + count);
            contentValues.put(CallLog.Calls.DATE, System.currentTimeMillis());
            contentValues.put(CallLog.Calls.DURATION, count * 10);
            contentValues.put(CallLog.Calls.NEW, "0");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
                return;
            contentResolver.insert(CallLog.Calls.CONTENT_URI, contentValues);
            if(count%5 == 0) {
                savedTimes = count;
                handler.sendEmptyMessage(CURRENT_TIMES);
            }
        }
        handler.sendEmptyMessage(FILL_CALL_COMPLETED);
        count = 1;
        times = 0;
        timer = System.currentTimeMillis()-timer;
        Log.e(TAG,"Fill Call logs cost "+timer/1000+" seconds");
    }

    public void fillSMS(int num){
        timer = System.currentTimeMillis();
        uri = Uri.parse("content://sms/");
        for(;count<=num;count++) {
            contentValues.clear();
            contentValues.put("address",1234567);
            contentValues.put("type",1);
            contentValues.put("date", System.currentTimeMillis());
            contentValues.put("body","testmessage"+count);
            contentResolver.insert(uri, contentValues);
            if(count%5 == 0) {
                savedTimes = count;
                handler.sendEmptyMessage(CURRENT_TIMES);
            }
        }
        handler.sendEmptyMessage(FILL_SMS_COMPLETED);
        count = 1;
        times = 0;
        timer = System.currentTimeMillis()-timer;
        Log.e(TAG,"Fill SMS cost "+timer/1000+" seconds");
    }

    public void fillContacts(int num){
        timer = System.currentTimeMillis();
        long id;
        for(;count<=num;count++) {
            contentValues.clear();
            uri = contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
            id = ContentUris.parseId(uri);
            contentValues.clear();
            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, id);
            contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, "testcontact" + count);
            contentResolver.insert(ContactsContract.Data.CONTENT_URI, contentValues);
            contentValues.clear();
            contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, id);
            contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, "1234567" + count);
            contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            contentResolver.insert(ContactsContract.Data.CONTENT_URI, contentValues);
            if(count%3 == 0) {
                savedTimes = count;
                handler.sendEmptyMessage(CURRENT_TIMES);
            }
        }
        handler.sendEmptyMessage(FILL_CONTACTS_COMPLETED);
        count = 1;
        times = 0;
        timer = System.currentTimeMillis()-timer;
        Log.e(TAG,"Fill contacts cost "+timer/1000+" seconds");
    }

    public void checkPermissions(){
        ArrayList<String> permissions = new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG,"申请联系人权限");
            permissions.add(Manifest.permission.WRITE_CONTACTS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG,"申请短信权限");
            permissions.add(Manifest.permission.READ_SMS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG,"申请通话记录权限");
            permissions.add(Manifest.permission.WRITE_CALL_LOG);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG,"申请日历权限");
            permissions.add(Manifest.permission.WRITE_CALENDAR);
        }
        if(permissions.size() > 0) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), 1);
        }
    }
}
