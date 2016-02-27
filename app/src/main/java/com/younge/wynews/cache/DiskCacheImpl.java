package com.younge.wynews.cache;

import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Allen Lake on 2016/2/26 0026.
 * IDiskC ache的实现类
 * 用于将数据保存在磁盘，以及将文件内容获取出来
 */
public class DiskCacheImpl implements IDiskCache{

    //需要保存或者获取到的文件夹
    private static final String FOLDER = "stx" + File.separator + "cache";
    //定义一个文件夹
    private File folder;

    public DiskCacheImpl(){
        if (!isMounted()) {
            throw new IllegalStateException("磁盘挂载异常");
        }
        //获取sd根路径的绝对路径
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folder;
        //实例化一个文件
        folder = new File(absolutePath + File.separator + FOLDER);
        if(!folder.exists() || folder.isFile()){
            folder.mkdir();
        }

    }

    /**
     * 将文件保存到硬盘
     * @param url  地址
     * @param data 数据
     */
    @Override
    public void put(String url, byte[] data) {
        if(url == null || data == null){
            return;
        }
        String fileName = getFileName(url);
        if(TextUtils.isEmpty(fileName)){
           return;
        }
        File file = new File(folder,fileName);
        //保存到文件
        savaToFile(file,data);
    }

    /**
     * 获取文件
     * @param url 地址
     * @return
     */
    @Override
    public byte[] get(String url) {
        byte [] ret = null;
        if(TextUtils.isEmpty(url)){
                return ret;
        }

        //获取这个文件对象
        String fileName = getFileName(url);
        if(TextUtils.isEmpty(fileName)){
            return ret;
        }
        File file = new File(folder,fileName);
        //获取文件中的内容
        ret = getDataFromFile(file);
        return ret;
    }

    /**
     * 判断是否挂载SD卡
     *
     * @return
     */
    private boolean isMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取文件名
     * @param url
     * @return
     */
    private String getFileName(String url){
        if(TextUtils.isEmpty(url)){
            return null;
        }
        //url 也是一种uri，所以可以通过这个方法，获取到最后一块的文件名
        String lastPathSegment = Uri.parse(url).getLastPathSegment();
        //如果图片没有最后的文件名则把url地址作为文件名
        if (lastPathSegment == null) {
            return null;
        }
        return lastPathSegment;
    }

    /**
     * 保存到文件
     * @param file
     * @param data
     */
    private void savaToFile(File file,byte[] data){
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        Log.d("fileName:",file.getPath());
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeStream(fos,bos);
        }
    }


    /**
     * 关闭流
     *
     * @param closeables
     */
    private void closeStream(Closeable... closeables) {
        if (closeables != null) {
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

    /**
     * 获取文件中的内容
     * @param file
     * @return
     */
    private byte[] getDataFromFile(File file) {
        byte[] ret = null;
        //如果不存在，或者不是一个文件，直接返回
        if (!file.isFile()) {
            return ret;
        }
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;

        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            int len = 0;
            byte[] buffer = new byte[512];
            while ((len=fis.read(buffer)) != -1){
                bos.write(buffer,0,len);
                bos.flush();
            }
            ret = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeStream(fis,bos);
        }
        return ret;
    }
}
