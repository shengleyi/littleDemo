package com.example.gojie.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gojie.coolweather.R;
import com.example.gojie.coolweather.service.AutoUpdateService;
import com.example.gojie.coolweather.utils.HttpUtils;
import com.example.gojie.coolweather.utils.Utility;

/**
 * Created by gojie on 2017/3/25.
 */

public class WeatherActivity extends Activity implements View.OnClickListener{

    private TextView tv_cityname;
    private TextView tv_ptime;
    private LinearLayout ll_weather_info;
    private TextView tv_current_date;
    private TextView tv_weather_desp;
    private TextView temp1;
    private TextView temp2;

    private TextView btn_change;
    private TextView btn_refresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather);
        //初始化控件
        tv_cityname = (TextView)findViewById(R.id.city_name);
        tv_ptime = (TextView)findViewById(R.id.publish_text);
        ll_weather_info = (LinearLayout)findViewById(R.id.weather_info_layout);
        tv_current_date = (TextView)findViewById(R.id.current_date);
        tv_weather_desp = (TextView)findViewById(R.id.weather_desp);
        temp1 = (TextView)findViewById(R.id.temp1);
        temp2 = (TextView)findViewById(R.id.temp2);

        btn_change = (TextView)findViewById(R.id.change_city);
        btn_refresh = (TextView)findViewById(R.id.refresh_weather);

        String countyCode = getIntent().getStringExtra("countyCode");

        if(!TextUtils.isEmpty(countyCode)){
            //有县级代号就去查询天气
            tv_ptime.setText("同步中...");
            ll_weather_info.setVisibility(View.INVISIBLE);
            tv_cityname.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else{
            showWeather();
        }
        btn_change.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_city:
                Intent i = new Intent(this,ChooseAreaActivity.class);
                i.putExtra("from_weather_activity",true);
                startActivity(i);
                finish();
                break;
            case R.id.refresh_weather:
                tv_ptime.setText("同步中...");
                SharedPreferences pres = PreferenceManager.getDefaultSharedPreferences(this);

                String weatherCode = pres.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }


    private void queryWeatherCode(String countryCode){
        String address = "http://www.weather.com.cn/data/list3/city"+countryCode+".xml";

        queryFromServer(address,"countryCode");
    }

    private void queryWeatherInfo(String weatherCode){
        String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";

        queryFromServer(address,"weatherCode");
    }

    private void queryFromServer(String url, final String type){
        HttpUtils.sendHttpRequest(url, new HttpUtils.HttpCallBackListener() {
            @Override
            public void onFinish(String responds) {
                if(type.equals("countryCode")){
                    if(!TextUtils.isEmpty(responds)){
                        //从服务器返回的数据中解析出天气代号
                        String[] array = responds.split("\\|");
                        if(array!=null&&array.length == 2){
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }

                    }
                }else{
                    //处理服务器返回的天气信息
                    Utility.handleWeatherRespond(responds,WeatherActivity.this);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void OnError(Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_ptime.setText("同步失败");
                    }
                });
            }
        });
    }

    /**
     * 从sharedPreferences文件中读取存储的天气信息，并显示在在界面上
     */
    private void showWeather(){
        SharedPreferences pres = PreferenceManager.getDefaultSharedPreferences(this);

        tv_cityname.setText(pres.getString("city_name",""));
        temp1.setText(pres.getString("temp1",""));
        temp2.setText(pres.getString("temp2",""));
        tv_weather_desp.setText(pres.getString("weather_desp",""));
        tv_ptime.setText("今天"+pres.getString("publish_time","")+"发布");
        tv_current_date.setText(pres.getString("current_date",""));
        tv_cityname.setVisibility(View.VISIBLE);
        ll_weather_info.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}
