package com.example.gojie.coolweather.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gojie.coolweather.R;
import com.example.gojie.coolweather.db.CoolWeatherDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gojie on 2017/3/25.
 */

public class ChooseAreaActivity extends Activity {
    private CoolWeatherDB coolWeatherDB;
    private TextView tv_title;
    private ListView list_view;
    private ArrayAdapter adapter;
    private List<String> dataList = new ArrayList<>();

    /** 省级数据 */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choosearea);
        tv_title = (TextView)findViewById(R.id.title_text);
        list_view = (ListView)findViewById(R.id.list_view);
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,dataList);
        list_view.setAdapter(adapter);

        coolWeatherDB = CoolWeatherDB.getInstance(this);

        loadProvinceData();


    }


    private void loadProvinceData(){

    }
}
