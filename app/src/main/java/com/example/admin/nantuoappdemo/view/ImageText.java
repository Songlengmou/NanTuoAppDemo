package com.example.admin.nantuoappdemo.view;


import com.example.admin.nantuoappdemo.R;

import java.util.HashMap;

/**
 * Created by admin on 2017/12/11.
 */

public class ImageText {
    static HashMap<String, Integer> img = new HashMap<>();

    public static void setImg() {
        img.put("[ab1]", R.drawable.bas);
        img.put("[ab2]", R.drawable.bb);
        img.put("[ab3]", R.drawable.bc);
        img.put("[ab4]", R.drawable.bd);
    }

    public static int getImg(String key) {
        if (img.size() == 0) {
            setImg();
        }
        return img.get(key);
    }
}
