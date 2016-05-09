package com.test.autowakeup;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;

import java.io.IOException;

public class MyService extends Service {

    long interval = 6000;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("debug","启动测试");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("debug", "自动亮屏");
        final PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "wakeup");
        wakeLock.acquire();
        //解锁
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.isRun) {
                    Intent i = new Intent(MyService.this, MyService.class);
                    PendingIntent pendingIntent = PendingIntent.getService(MyService.this, 0, i, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, pendingIntent);

                    Log.e("debug", "自动灭屏");
                    if (powerManager.isScreenOn()) {
                        try {
                            wakeLock.release();
                            Runtime.getRuntime().exec("input keyevent KEYCODE_POWER");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        },interval);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("debug","停止测试");
    }
}
