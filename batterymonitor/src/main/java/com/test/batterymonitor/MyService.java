package com.test.batterymonitor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends Service {

    String TAG = "debug";
    String charge,status;
    int voltage, percent, temperature, lastPercent = 101;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
                percent = intent.getIntExtra("level",0);
                if(percent == lastPercent)
                    return;
                else
                    lastPercent = percent;
                Log.e(TAG,"BroadcastReceiver onReceive: "+intent.getAction());
                temperature = intent.getIntExtra("temperature",0);
                voltage = intent.getIntExtra("voltage",0);
                switch (intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN))
                {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        charge = "充电中";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        charge = "放电中";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        charge = "未充电";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        charge = "已充满";
                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        charge = "未知状态";
                        break;
                }
                switch (intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN))
                {
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        status = "未知错误";
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        status = "状态良好";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        status = "电池没电";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        status = "电压过高";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        status =  "电池过热";
                        break;
                }
                try {
                    File file = new File("/sdcard/battery.txt");
                    FileOutputStream fileOutputStream = new FileOutputStream(file,true);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    bufferedWriter.write(getTime()+", "+percent+"%, "+voltage+" mV, "
                            +new DecimalFormat("0.0").format(temperature*0.1)+"度, "+charge+", "+status);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStreamWriter.close();
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"MyService onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        Log.e(TAG,"MyService onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"MyService onStartCommand");
        registerReceiver(broadcastReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return super.onStartCommand(intent, flags, startId);
    }

    public String getTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        return time;
    }
}
