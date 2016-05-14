package com.test.switchonoff;

import android.bluetooth.BluetoothAdapter;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView status;
    Button wifi,bt,flight,stop,gps;
    boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = (TextView) findViewById(R.id.status);
        wifi = (Button) findViewById(R.id.wifi);
        bt = (Button) findViewById(R.id.bt);
        flight = (Button) findViewById(R.id.flight);
        stop = (Button) findViewById(R.id.stop);
        gps = (Button) findViewById(R.id.gps);

        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = true;
                final WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                new Thread(){
                    @Override
                    public void run() {
                        while (running) {
                            if (wifiManager.isWifiEnabled())
                                wifiManager.setWifiEnabled(false);
                            else
                                wifiManager.setWifiEnabled(true);
                            try {
                                sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = true;
                final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                new Thread() {
                    @Override
                    public void run() {
                        while (running) {
                            if (bluetoothAdapter.isEnabled())
                                bluetoothAdapter.disable();
                            else
                                bluetoothAdapter.enable();
                            try {
                                sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            }
        });

        flight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = true;
                new Thread() {
                    @Override
                    public void run() {
//                        while (running) {
//                        }
                    }
                }.start();
            }
        });

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = true;
                new Thread() {
                    @Override
                    public void run() {
                        while (running) {
                        }
                    }
                }.start();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = false;
            }
        });
    }
}
