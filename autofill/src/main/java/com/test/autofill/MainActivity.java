package com.test.autofill;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.storage.StorageManager;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    Button fill, clear;
    RadioGroup radioGroup;
    TextView percent;
    EditText editText;
    boolean isFill,isCompleted;
    int targetPercent = 0;
    float percentage;
    static final int GET_PERCENT = 1;
    static final int FILL_COMPLETED = 2;
    static final int DELETE_FILE_COMPLETED = 3;
    Handler handler;
    File fillFile;
    InputMethodManager inputMethodManager;
    static final String TAG = "debug";
    static String fillContent = "";
    long timer;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clear = (Button) findViewById(R.id.clear);
        radioGroup = (RadioGroup) findViewById(R.id.path);
        fill = (Button) findViewById(R.id.fill);
        fill.setBackgroundColor(Color.GREEN);
        clear.setBackgroundColor(Color.RED);
        percent = (TextView) findViewById(R.id.percent);
        editText = (EditText) findViewById(R.id.editText);
        percent.setText(getPercent());
        final File file[] = getObbDirs();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        for(int i=0;i<100;i++)
            fillContent = fillContent + "这是测试填充的内容";

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GET_PERCENT:
                        percent.setText(getPercent());
                        break;
                    case FILL_COMPLETED:
                        percent.setText(getPercent());
                        fill.setText("fill");
                        fill.setBackgroundColor(Color.GREEN);
                        clear.setClickable(true);
                        clear.setBackgroundColor(Color.RED);
                        if(isCompleted == true) {
                            timer = System.currentTimeMillis() - timer;
                            timer = timer / 1000;
                            Log.e(TAG,"Duration of fill is "+timer+" seconds");
                            Toast.makeText(MainActivity.this, "填充存储完成", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case DELETE_FILE_COMPLETED:
                        percent.setText(getPercent());
                        fill.setClickable(true);
                        fill.setBackgroundColor(Color.GREEN);
                        break;
                }
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.internal){
                    fillFile = new File(file[0],"fill.dat");
                    Log.e(TAG,"Path is internal storage: "+fillFile.getPath());
                }
                else {
                    fillFile = new File(file[1],"fill.dat");
                    Log.e(TAG,"Path is SD storage: "+fillFile.getPath());
                }
            }
        });

        fill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fill.getText().equals("fill")){
                    timer = System.currentTimeMillis();
                    clear.setClickable(false);
                    clear.setBackgroundColor(Color.GRAY);
                    fill.setText("stop");
                    fill.setBackgroundColor(Color.RED);
                    isFill = true;
                    inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
                else{
                    isFill = false;
                    fill.setText("fill");
                    fill.setBackgroundColor(Color.GREEN);
                }
                if(!TextUtils.isEmpty(editText.getText()))
                    targetPercent = Integer.parseInt(editText.getText().toString());
                if(targetPercent < 0 || targetPercent > 99) {
                    Toast.makeText(MainActivity.this, "请输入0至99", Toast.LENGTH_SHORT).show();
                    isFill = false;
                }
                new Thread(){
                    @Override
                    public void run() {
                        while(isFill){
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            handler.sendEmptyMessage(GET_PERCENT);
                        }
                    }
                }.start();
                new Thread(){
                    @Override
                    public void run() {
                        while (isFill) {
                            percentage = (float)(getTotalInternalMemorySize()-getAvailableInternalMemorySize())/getTotalInternalMemorySize()*100;
                            if ( percentage > targetPercent) {
                                isFill = false;
                                isCompleted = true;
                            }
                        }
                        handler.sendEmptyMessage(FILL_COMPLETED);
                    }
                }.start();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(fillFile,true);
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                            while(isFill){
                                bufferedWriter.write(fillContent);
                            }
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
                }.start();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fill.setClickable(false);
                fill.setBackgroundColor(Color.GRAY);
                new Thread(){
                    @Override
                    public void run() {
                        fillFile.delete();
                        isCompleted = false;
                        handler.sendEmptyMessage(DELETE_FILE_COMPLETED);
                    }
                }.start();
            }
        });
    }

    public String getSdPath() {
        try {
            Class<?> c = null;
            Object object = null;
            StorageManager sm = (StorageManager) getSystemService(STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", c);
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm, object);
            // second element in paths[] is secondary storage path
            return paths[1];
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPercent(){
        String percent = new DecimalFormat("0.00")
                .format((float) (getTotalInternalMemorySize() - getAvailableInternalMemorySize()) / getTotalInternalMemorySize() * 100);
        return percent+"%";
    }

    public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

}
