package com.example.gojie.coolweather.utils;

import android.text.TextUtils;

import com.example.gojie.coolweather.db.CoolWeatherDB;
import com.example.gojie.coolweather.model.CityModel;
import com.example.gojie.coolweather.model.CountryModel;
import com.example.gojie.coolweather.model.ProvinceModel;

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
}
