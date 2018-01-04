package com.example.admin.nantuoappdemo.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.example.admin.nantuoappdemo.callbreak.LoadListener;


/**
 * Created by admin on 2017/12/11.
 */

public class MyRefreshLayout extends SwipeRefreshLayout {
    private boolean loading = false;

    public MyRefreshLayout(Context context) {
        super(context);
    }

    public MyRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //加载更多时 view是否可见
    public void setLoadingView(boolean isLoading) {
        loading = isLoading;
        loadListener.setFootView(isLoading);
    }

    private LoadListener loadListener;

    public void setLoadListener(LoadListener loadListener) {
        this.loadListener = loadListener;
    }
}
