package com.example.gojie.coolweather.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gojie on 2017/3/25.
 */

public class HttpUtils {

    public interface HttpCallBackListener{
        void onFinish(String responds);
        void OnError(Exception e);
    }

    public static void sendHttpRequest(final String addressUrl, final HttpCallBackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    URL url = new URL(addressUrl);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = reader.readLine())!=null){
                        response.append(line);
                    }

                    if(listener !=null){
                        listener.onFinish(response.toString());
                    }
                }catch (Exception e){
                    if(listener!=null){
                        listener.OnError(e);
                    }
                }finally {
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
