package com.example.admin.nantuoappdemo.rx;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 2017/12/8.
 */

public class RenZhen_SP {
    private static final String SHARE_PREFS_NAME = "NanTuo";

    public static void putBoolean(Context ctx, String key, boolean value) {
        SharedPreferences pref = ctx.getSharedPreferences(SHARE_PREFS_NAME,
                Context.MODE_PRIVATE);

        pref.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context ctx, String key,
                                     boolean defaultValue) {
        SharedPreferences pref = ctx.getSharedPreferences(SHARE_PREFS_NAME,
                Context.MODE_PRIVATE);

        return pref.getBoolean(key, defaultValue);
    }
}
