package com.test.autoreboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)){
            Intent mainAty=new Intent(context,MainActivity.class);
            mainAty.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(context.getSharedPreferences("times",Context.MODE_PRIVATE).getInt("times",0) != 0)
                context.startActivity(mainAty);
        }
    }
}
