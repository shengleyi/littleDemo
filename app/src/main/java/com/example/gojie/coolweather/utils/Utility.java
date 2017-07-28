package com.example.gojie.coolweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.gojie.coolweather.db.CoolWeatherDB;
import com.example.gojie.coolweather.model.CityModel;
import com.example.gojie.coolweather.model.CountryModel;
import com.example.gojie.coolweather.model.ProvinceModel;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by gojie on 2017/3/25.
 */

public class Utility {

    public synchronized static boolean handleProvinceRespond(CoolWeatherDB coolWeatherDB,String respond){
        if(!TextUtils.isEmpty(respond)){
            String[] allProvince = respond.split(",");
            if(allProvince!=null && allProvince.length>0){
                for(String p:allProvince){
                    String[] array = p.split("\\|");
                    ProvinceModel bean = new ProvinceModel();
                    bean.setProvinceName(array[1]);
                    bean.setProvinceCode(array[0]);

                    coolWeatherDB.saveProvince(bean);
                }
                return true;
            }
        }
        return false;
    }


    public synchronized static boolean handleCityRespond(CoolWeatherDB coolWeatherDB,String respond,int provinceId){
        if(!TextUtils.isEmpty(respond)){
            String[] allCity = respond.split(",");
            if(allCity!=null&& allCity.length>0){
                for(String c:allCity){
                    String[] array = c.split("\\|");
                    CityModel bean = new CityModel();
                    bean.setCityCode(array[0]);
                    bean.setCityName(array[1]);
                    bean.setProvinceId(provinceId);

                    coolWeatherDB.saveCity(bean);
                }
                return true;
            }
        }
        return false;
    }


    public synchronized static boolean handleCountryRespond(CoolWeatherDB coolWeatherDB,String respond,int cityId){
        if(!TextUtils.isEmpty(respond)){
            String[] allCountry = respond.split(",");
            if(allCountry!=null && allCountry.length>0){
                for(String c:allCountry){
                    String[] array = c.split("\\|");
                    CountryModel bean = new CountryModel();
                    bean.setCountryCode(array[0]);
                    bean.setCountryName(array[1]);
                    bean.setCityId(cityId);

                    coolWeatherDB.saveCountry(bean);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 处理天气返回的数据
     */
    public static void handleWeatherRespond(String respond,Context context){
        try {
            JSONObject jsonObject = new JSONObject(respond);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weaatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");

            saveWeatherInfo(context,cityName,weaatherDesp,weatherCode,temp1,temp2,publishTime);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void saveWeatherInfo(Context context,String cityName,String weatherDesp,
                                 String weatherCode,String temp1,String temp2,String ptime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",ptime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }
}
