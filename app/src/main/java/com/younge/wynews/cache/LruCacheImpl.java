package com.younge.wynews.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

/**
 * Created by Allen Lake on 2016/2/26 0026.
 * 实现了ILruCacher接口，作为内存缓存
 */
public class LruCacheImpl implements ILruCache{

    //用于内存缓存
    private LruCache<String, Bitmap> lruCache;

    public LruCacheImpl(){
        //获取到当前App最大内存的几分之几
        //这个大小，根据app的图片存储多少来决定
        long totalSize = Runtime.getRuntime().maxMemory() / 8;
        //实例化LruCache
        lruCache = new LruCache<String, Bitmap>((int) totalSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //获取到每一张的图片占的字节数组
                return value.getByteCount();
            }
        };
    }

    /**
     * 通过url获取bitmap
     * @param url
     * @return
     */
    @Override
    public Bitmap get(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return lruCache.get(url);
    }

    /**
     * 保存图片到内存
     * @param url
     * @param bitmap
     */
    @Override
    public void put(String url, Bitmap bitmap) {
        //如果有一个为null，那么直接返回
        if (TextUtils.isEmpty(url) || bitmap == null) {
            return;
        }
        //如果以前没有这个东西，那么才添加
        if (lruCache.get(url) == null) {
            lruCache.put(url, bitmap);
        }
    }
}
