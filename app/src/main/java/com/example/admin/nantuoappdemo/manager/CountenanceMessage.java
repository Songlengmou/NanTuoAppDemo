package com.example.admin.nantuoappdemo.manager;


import com.example.admin.nantuoappdemo.callbreak.SendListener;

/**
 * Created by admin on 2017/12/11.
 * 表情包
 */

public class CountenanceMessage {
    private SendListener sendListener;

    private static CountenanceMessage ourInstance = null;

    public SendListener getSendListener() {
        return this.sendListener;
    }

    public void setSendListener(SendListener sendListener) {
        this.sendListener = sendListener;
    }

    public synchronized static CountenanceMessage getInstance() {
        if (ourInstance == null) {
            ourInstance = new CountenanceMessage();
        }
        return ourInstance;
    }
}
