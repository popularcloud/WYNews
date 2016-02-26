package com.younge.wynews.utils;

import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Allen Lake on 2016/2/26 0026.
 * 网络访问处理类
 */
public class HttpUtils {
    public static final int TIMEOUT_MILLIS = 10000;
    private static final int TIME_OUT = 10000;//10秒超时
    //运送的Handler
    private static final Handler M_HANDLER = new Handler();


    public static void downLoadData(final String url, final OnFetchDataListener onFetchDataListener){
        if(url == null || onFetchDataListener == null){
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL netUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) netUrl.openConnection();
                    //设置请求方法
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    //设置连接超时时间
                    connection.setConnectTimeout(TIME_OUT);
                    //设置读取超时时间
                    connection.setReadTimeout(TIME_OUT);
                    //开始连接
                    connection.connect();
                    //访问成功，响应正常
                    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        //网络数据获取成功
                        InputStream i = connection.getInputStream();
                        //将输入流装换为字节数组
                        final byte[] data = isToByteArray(i);
                        //运送结果给主线程
                        M_HANDLER.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("====>", "我没进回调方法了。。。。。");
                                //这个方法里面的语句，是执行在主线程的
                                if (data != null) {
                                    onFetchDataListener.OnFetch(url, data);
                                    Log.i("====>", "我进回调方法了。。。。。");
                                }
                            }
                        });
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private static byte[] isToByteArray(InputStream is){

        byte[] ret = null;
        ByteArrayOutputStream bos = null;
       try {
           bos = new ByteArrayOutputStream();
           byte[] buff = new byte[1024 * 4];
           int len = 0;
           while ((len = is.read(buff)) != -1){
               bos.write(buff,0,len);
               bos.flush();
           }
           ret = bos.toByteArray();
       }catch (IOException e){
            e.printStackTrace();
       }finally {
           closeStream(is,bos);
       }
        return ret;
    }

    //关闭流
    private static void closeStream(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //网络下载的回调
    public interface OnFetchDataListener {
        void OnFetch(String url, byte[] result);
    }

    //服务器响应处理回调接口
    public interface OnFetchResponseListener {
        void OnFechResponse(String reponse);
    }
}
