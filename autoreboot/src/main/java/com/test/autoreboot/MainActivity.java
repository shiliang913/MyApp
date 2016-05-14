package com.test.autoreboot;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    boolean isReboot = true;
    Button start;
    TextView notice,times;
    int currentTimes = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public MainActivity(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        notice = (TextView) findViewById(R.id.notice);
        times = (TextView) findViewById(R.id.times);
        sharedPreferences = getSharedPreferences("times",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        final android.os.Handler handler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (isReboot) {
                    PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                    powerManager.reboot(null);
                }
            }
        };

        currentTimes = sharedPreferences.getInt("times", 0);
        if (currentTimes == 0) {
            start.setText("start");
            start.setBackgroundColor(Color.GREEN);
            notice.setText("点击START开始测试");
            times.setText("0");
        } else {
            start.setText("stop");
            start.setBackgroundColor(Color.RED);
            notice.setText("重启中，请勿操作");
            times.setText(++currentTimes + "");
            writeTimes(currentTimes);
            new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(1);
                }
            }.start();
        }

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start.getText().equals("start")) {
                    writeTimes(1);
                    isReboot = true;
                    times.setText("1");
                    start.setBackgroundColor(Color.RED);
                    start.setText("stop");
                    notice.setText("重启中，请勿操作");
                } else {
                    isReboot = false;
                    start.setText("start");
                    start.setBackgroundColor(Color.GREEN);
                    notice.setText("点击START开始测试");
                    writeTimes(0);
                }
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(1);
                    }
                }.start();
            }
        });
    }

    public void writeTimes(int time){
        editor.putInt("times",time);
        editor.commit();
    }
}