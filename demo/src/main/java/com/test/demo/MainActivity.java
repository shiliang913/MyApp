package com.test.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,AbsListView.OnScrollListener{

    ArrayAdapter<String> arrayAdapter;
    SimpleAdapter simpleAdapter;
    android.widget.ListView listView;
    ArrayList<Map<String,Object>> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (android.widget.ListView) findViewById(R.id.listView);
        list = new ArrayList<Map<String,Object>>();
//        String data[] = new String[30];
//        for (int i=0;i<30;i++){
//            data[i] = i+"这是填充的数据";
//        }
//        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        for (int i=0;i<30;i++){
            Map<String,Object> map = new HashMap<>();
            map.put("image",R.mipmap.ic_launcher);
            map.put("text",i+"这是填充的数据");
            list.add(map);
        }
        simpleAdapter = new SimpleAdapter(this,list,R.layout.simple,new String[]{"image","text"},new int[]{R.id.image,R.id.text});

        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(this);
//        listView.setOnScrollListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("debug","......................");
        String text = listView.getItemAtPosition(position)+"";
        Toast.makeText(this,text+""+position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
