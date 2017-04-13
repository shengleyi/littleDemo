package com.example.gojie.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gojie.coolweather.model.CityModel;
import com.example.gojie.coolweather.model.CountryModel;
import com.example.gojie.coolweather.model.ProvinceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gojie on 2017/3/25.
 */

public class CoolWeatherDB {
    public static final String DB_NAME = "cool_weather";

    public static final int VERSION = 1;

    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;

    private CoolWeatherDB(Context context){
        CoolWeatherOpenDataBase dbHelper = new CoolWeatherOpenDataBase(context,DB_NAME,null, VERSION);
        db = dbHelper.getWritableDatabase();
    }


    /**
     * 获取CoolWeatherDB的实例
     */

    public synchronized static CoolWeatherDB getInstance(Context context){
        if(coolWeatherDB == null){
            coolWeatherDB = new CoolWeatherDB(context);

        }

        return coolWeatherDB;
    }


    /**
     * 将province存储到数据库
     */
    public void saveProvince(ProvinceModel pData){
        if(pData !=null){
            ContentValues values = new ContentValues();
            values.put("province_name",pData.getProvinceName());
            values.put("province_code",pData.getProvinceCode());
            db.insert("Province",null,values);

        }
    }

    /**
     * 获取全国的省份信息
     */
    public List<ProvinceModel> getProvince(){
        List<ProvinceModel> list = new ArrayList<>();
        Cursor cursor = db.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                ProvinceModel bean = new ProvinceModel();
                bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                bean.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                bean.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                list.add(bean);
            }while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 保存市信息
     */
    public void saveCity(CityModel cityData){
        if(cityData!=null){
            ContentValues values = new ContentValues();
            values.put("city_name",cityData.getCityName());
            values.put("city_code",cityData.getCityCode());
            values.put("province_id", cityData.getProvinceId());
            db.insert("City",null,values);
        }
    }


    /**
     * 获取市信息
     */

    public List<CityModel> getCity(int provinceId){
        List<CityModel> list = new ArrayList<>();
        Cursor cursor = db.query("City",null,"province_id",new String[]{String.valueOf(provinceId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                CityModel bean = new CityModel();
                bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                bean.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                bean.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                bean.setProvinceId(provinceId);
                list.add(bean);
            }while (cursor.moveToNext());
        }
        return list;
    }


    /**
     * 保存县信息
     */
    public void saveCountry(CountryModel countryData){
        if(countryData!=null){
            ContentValues values = new ContentValues();
            values.put("city_name",countryData.getCountryName());
            values.put("city_code",countryData.getCountryCode());
            values.put("city_id",countryData.getCityId());
            db.insert("Country",null,values);
        }
    }


    /**
     * 获取县信息
     */

    public List<CountryModel> getCountry(int cityId){
        List<CountryModel> list = new ArrayList<>();
        Cursor cursor = db.query("COuntry",null,"city_id",new String[]{String.valueOf(cityId)},null,null,null);
        if(cursor.moveToFirst()){
            do {
                CountryModel bean = new CountryModel();
                bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                bean.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
                bean.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
                bean.setCityId(cityId);
                list.add(bean);
            }while (cursor.moveToNext());
        }
        return list;
    }

}
