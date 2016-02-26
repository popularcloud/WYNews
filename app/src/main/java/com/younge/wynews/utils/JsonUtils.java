package com.younge.wynews.utils;

import com.younge.wynews.entity.ChapterListItem;
import com.younge.wynews.entity.GameListItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allen Lake on 2016/2/26 0026.
 */
public class JsonUtils {
   /** 解析文章json数据的方法
    *
            * @param json 网络下载的json数据
    * @return
            */
    public static List<ChapterListItem> parseChapterJson(String json) {
        List<ChapterListItem> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(json);
            JSONObject data = (JSONObject) object.get("data");
            for (int index = 0; index < data.length(); index++) {
                //根据键值对来进行json解析
                JSONObject jsonObject = (JSONObject) data.get(index + "");
                String id = jsonObject.getString("id");
                String typeid = jsonObject.getString("typeid");
                String title = jsonObject.getString("title");
                String senddate = jsonObject.getString("senddate");
                String litpic = jsonObject.getString("litpic");
                String arcurl = jsonObject.getString("arcurl");
                String feedback = jsonObject.getString("feedback");
                ChapterListItem chapterListItem = new ChapterListItem(id, typeid, title, senddate, litpic, feedback, arcurl);
                list.add(chapterListItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 游戏列表json解析
     *
     * @param json
     * @return
     */
    public static List<GameListItem> parseGameJson(String json) {
        List<GameListItem> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(json);
            JSONObject data = (JSONObject) object.get("data");
            for (int index = 0; index < data.length(); index++) {
                //根据键值对来进行json解析
                JSONObject jsonObject = (JSONObject) data.get(index + "");
                String id = jsonObject.getString("id");
                String typeid = jsonObject.getString("typeid");
                String title = jsonObject.getString("title");
                String senddate = jsonObject.getString("senddate");
                String litpic = jsonObject.getString("litpic");
                String arcurl = jsonObject.getString("arcurl");
                String keywords = jsonObject.getString("keywords");
                String description = jsonObject.getString("description");
                String typename = jsonObject.getString("typename");
                String language = jsonObject.getString("language");
                GameListItem gameListItem = new GameListItem(id, typeid, title, litpic, senddate, keywords, description, typename, language, arcurl);
                list.add(gameListItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
