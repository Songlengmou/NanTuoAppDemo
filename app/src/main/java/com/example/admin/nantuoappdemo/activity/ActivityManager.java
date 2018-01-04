package com.example.admin.nantuoappdemo.activity;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by admin on 2017/12/28.
 * <p>
 * Activity的管理栈
 */

public class ActivityManager {
    //单例模式：饿汉式
    public ActivityManager() {
    }

    private static ActivityManager activityManager = new ActivityManager();

    public static ActivityManager getInstance() {
        return activityManager;
    }

    //提供栈对象
    private Stack<Activity> activityStack = new Stack<>();

    //Activity的添加
    public void add(Activity activity) {
        if (activity != null) {
            activityStack.add(activity);
        }
    }

    //删除指定的Activity
    public void remove(Activity activity) {
        if (activity != null) {
//            for (int i = 0; i < activityStack.size(); i++) {
//                Activity currentActivity = activityStack.get(i);
//                if (currentActivity.getClass().equals(activity.getClass())) {
//                    currentActivity.finish();//销毁当前的Activity
//                    activityStack.remove(i);// 从栈空间移除
//                }
//            }
            for (int i = activityStack.size() - 1; i >= 0; i--) {
                Activity currentActivity = activityStack.get(i);
                if (currentActivity.getClass().equals(activity.getClass())) {
                    currentActivity.finish();//销毁当前的Activity
                    activityStack.remove(i);// 从栈空间移除
                }
            }
        }
    }

    //删除当前的Activity
    public void removeCurrent() {
//        Activity activity = activityStack.get(activityStack.size() - 1);
//        activity.finish();
//        activityStack.remove(activityStack.size() - 1);

        Activity activity = activityStack.lastElement();
        activity.finish();
        activityStack.remove(activity);
    }

    //删除所有
    public void removeAll() {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            activity.finish();
            activityStack.remove(activity);
        }
    }

    //返回栈的大小
    public int size() {
        return activityStack.size();
    }
}
