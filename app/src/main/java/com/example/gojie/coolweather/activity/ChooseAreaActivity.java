package com.example.gojie.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gojie.coolweather.R;
import com.example.gojie.coolweather.db.CoolWeatherDB;
import com.example.gojie.coolweather.model.CityModel;
import com.example.gojie.coolweather.model.CountryModel;
import com.example.gojie.coolweather.model.ProvinceModel;
import com.example.gojie.coolweather.utils.HttpUtils;
import com.example.gojie.coolweather.utils.Utility;

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
    private ProgressDialog progressDialog;

    /** 选中的省份 */
    private ProvinceModel selectProvince;

    /** 选中的市 */
    private CityModel selectCity;

    /** 省级数据 */
    private List<ProvinceModel> provinceLst;

    /** 市级数据 */
    private List<CityModel> cityLst;

    /** 县级数据 */
    private List<CountryModel> countryLst;


    /** 当前选中的级别 */
    private int currentLevel;

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pres = PreferenceManager.getDefaultSharedPreferences(this);
        if(pres.getBoolean("city_selected",false)){
            Intent i = new Intent(this,WeatherActivity.class);
            startActivity(i);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choosearea);
        tv_title = (TextView)findViewById(R.id.title_text);
        list_view = (ListView)findViewById(R.id.list_view);
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,dataList);
        list_view.setAdapter(adapter);
       list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               if(currentLevel == LEVEL_PROVINCE){
                   selectProvince = provinceLst.get(position);
                   tv_title.setText(selectProvince.getProvinceName());
                   loadCityData();
               }else if(currentLevel == LEVEL_CITY){
                   selectCity = cityLst.get(position);
                   tv_title.setText(selectCity.getCityName());
                   loadCountryData();
               }else{
                   String countryCode = countryLst.get(position).getCountryCode();
                   Intent i = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                   i.putExtra("countyCode",countryCode);
                   startActivity(i);
                   finish();
               }
           }
       });

        tv_title.setText("中国");

        coolWeatherDB = CoolWeatherDB.getInstance(this);

        //加载省级数据
        loadProvinceData();


    }


    private void loadProvinceData(){
        //优先从数据库查询
        provinceLst = coolWeatherDB.getProvince();
        if(provinceLst.size()>0){
            dataList.clear();
            for(ProvinceModel entry:provinceLst){
                dataList.add(entry.getProvinceName());
            }

            adapter.notifyDataSetChanged();
            list_view.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            queryFromSever(null,"province");
        }
    }

    private void loadCityData(){
        cityLst = coolWeatherDB.getCity(selectProvince.getId());
        if(cityLst.size()>0){
            dataList.clear();
            for(CityModel entry:cityLst){
                dataList.add(entry.getCityName());
            }
            adapter.notifyDataSetChanged();
            list_view.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            queryFromSever(selectProvince.getProvinceCode(),"city");

        }
    }


    private void loadCountryData(){
        countryLst = coolWeatherDB.getCountry(selectCity.getId());
        if(countryLst.size()>0){
            dataList.clear();
            for(CountryModel entry:countryLst){
                dataList.add(entry.getCountryName());
            }
            adapter.notifyDataSetChanged();
            list_view.setSelection(0);
            currentLevel = LEVEL_COUNTRY;
        }else{
            queryFromSever(selectCity.getCityCode(),"country");
        }
    }

    /**
     * 从服务器查询数据
     * @param code
     * @param type
     */
    private void queryFromSever(final String code,final String type){
        String url = "";
        if(!TextUtils.isEmpty(code)){
            url = "http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else{
            url = "http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();
        HttpUtils.sendHttpRequest(url, new HttpUtils.HttpCallBackListener() {
            @Override
            public void onFinish(String responds) {
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceRespond(coolWeatherDB,responds);
                }else if("city".equals(type)){
                    result = Utility.handleCityRespond(coolWeatherDB,responds,selectProvince.getId());
                }else{
                    result = Utility.handleCountryRespond(coolWeatherDB,responds,selectCity.getId());
                }


                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if(type.equals("province")){
                                loadProvinceData();
                            }else if(type.equals("city")){
                                loadCityData();
                            }else{
                                loadCountryData();
                            }
                        }
                    });
                }else{
                    closeProgressDialog();
                    Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void OnError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }

        progressDialog.show();
    }


    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_PROVINCE){
            finish();
        }else if(currentLevel == LEVEL_CITY){
            loadProvinceData();
        }else if(currentLevel == LEVEL_COUNTRY){
            loadCityData();
        }
    }
}
