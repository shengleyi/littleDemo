package com.example.gojie.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.gojie.coolweather.utils.HttpUtils;
import com.example.gojie.coolweather.utils.Utility;

/**
 * Created by leyi on 2017/7/28.
 */

public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                upDateWeather();
            }
        }).start();

        AlarmManager manger = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour = 8*60*60*1000;//8小时的毫秒数
        long triggerAtTIme = SystemClock.elapsedRealtime()+anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getBroadcast(this,0,i,0);
        manger.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTIme,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void upDateWeather(){
        SharedPreferences pres = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = pres.getString("weather_code","");
        String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";

        HttpUtils.sendHttpRequest(address, new HttpUtils.HttpCallBackListener() {
            @Override
            public void onFinish(String responds) {
                Utility.handleWeatherRespond(responds,AutoUpdateService.this);
            }

            @Override
            public void OnError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
