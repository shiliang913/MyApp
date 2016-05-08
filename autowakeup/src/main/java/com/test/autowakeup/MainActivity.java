package com.test.autowakeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button start, stop;
    TextView status;
    Handler handler = new Handler(){};
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        status = (TextView) findViewById(R.id.status);
        final Intent intent = new Intent(MainActivity.this,MyService.class);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pendingIntent = PendingIntent.getService(MainActivity.this,0,intent,0);
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),10000,pendingIntent);
                Log.e("debug","alarm已经设置好");
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(intent);
                alarmManager.cancel(pendingIntent);
                Log.e("debug","停止alarm");
            }
        });
    }

}
