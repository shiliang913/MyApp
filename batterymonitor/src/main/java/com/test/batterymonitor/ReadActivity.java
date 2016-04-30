package com.test.batterymonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadActivity extends AppCompatActivity {

    File file = new File("/sdcard/battery.txt");
    TextView read;
    StringBuffer content = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        read = (TextView) findViewById(R.id.read);
        read.setMovementMethod(ScrollingMovementMethod.getInstance());

        if(!file.exists()){
            read.setGravity(Gravity.CENTER);
            read.setText("还未产生电池数据!");
        }
        else{
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while((line = bufferedReader.readLine()) != null){
                    content.append(line);
                    content.append("\n");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            read.setText(content.toString());
        }
    }
}
