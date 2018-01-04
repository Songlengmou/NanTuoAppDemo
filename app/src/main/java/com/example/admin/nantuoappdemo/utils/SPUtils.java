package com.example.admin.nantuoappdemo.utils;

import android.content.Context;

import static android.content.Context.MODE_PRIVATE;

public class SPUtils {
    private static final String SP_NAME = "user";
    private static final String SP_PASS = "password";
    private static final String LAST_LOGIN_USERNAME = "LastLoginUserName";
    private static final String LAST_LOGIN_PASSWORD = "LastLoginPassWord";

    public static void setLastLoginUserName(Context context, String userName) {

        context.getSharedPreferences(SP_NAME, MODE_PRIVATE)
                .edit()
                .putString(LAST_LOGIN_USERNAME, userName)
                .apply();
    }

    public static String getLastLoginUserName(Context context) {

        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE)
                .getString(LAST_LOGIN_USERNAME, "");

    }

    public static void setLastLoginPassword(Context context, String userName) {

        context.getSharedPreferences(SP_PASS, MODE_PRIVATE)
                .edit()
                .putString(LAST_LOGIN_PASSWORD, userName)
                .apply();
    }

    public static String getLastLoginPassword(Context context) {

        return context.getSharedPreferences(SP_PASS, MODE_PRIVATE)
                .getString(LAST_LOGIN_PASSWORD, "");

    }
}
