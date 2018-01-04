package com.example.admin.nantuoappdemo.callbreak;

/**
 * Created by admin on 2017/12/11.
 * <p>
 * 下拉加载数据的监听
 */

public interface LoadListener {
    //加载数据
    void load();

    //根据是否正在加载 判断view是否可见
    void setFootView(boolean loading);
}
