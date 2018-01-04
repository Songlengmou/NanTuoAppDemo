package com.example.admin.nantuoappdemo.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by admin on 2017/12/11.
 */

public class FuilUtils {
    /**
     * 查询所有图片
     *
     * @param context
     * @return 图片路径集合
     */
    public static ArrayList<String> getAllImg(Context context) {
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            //获取路径
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            //获取图片宽高
            String width = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
            String height = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
            list.add(data);
        }
        cursor.close();
        return list;
    }
}
