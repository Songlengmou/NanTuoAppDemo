package com.example.admin.nantuoappdemo.view;


import com.example.admin.nantuoappdemo.R;

import java.util.HashMap;

/**
 * Created by admin on 2017/12/11.
 */

public class Image {
    public static HashMap<String, Integer> hashMap = new HashMap<>();
    public static int[] s = {R.drawable.bas, R.drawable.bb,
            R.drawable.bc, R.drawable.bd, R.drawable.be, R.drawable.bf, R.drawable.tbg, R.drawable.bh,
            R.drawable.bi, R.drawable.bj, R.drawable.bk, R.drawable.bm, R.drawable.bn};
    public static String[] str = {"[惊讶]", "[委屈]", "[色]", "[脸红]", "[酷]", "[衰]", "[困]", "[委屈]", "[惊愕]", "[晕]", "[怒]", "[呲牙]", "[高兴]"};

    public static void setImg() {
        for (int j = 0; j < str.length; j++) {
            hashMap.put(str[j], s[j]);
        }
    }

    public static int getImae(String k) {
        if (hashMap.size() == 0) {
            setImg();
        }
        return hashMap.get(k);
    }
}
