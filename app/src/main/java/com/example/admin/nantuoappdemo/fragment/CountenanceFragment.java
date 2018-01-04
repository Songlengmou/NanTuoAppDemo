package com.example.admin.nantuoappdemo.fragment;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.social.PrivateMessageActivity;
import com.example.admin.nantuoappdemo.adapter.CountenanceAdapter;
import com.example.admin.nantuoappdemo.callbreak.SendListener;
import com.example.admin.nantuoappdemo.manager.CountenanceMessage;
import com.example.admin.nantuoappdemo.view.Image;

import java.util.ArrayList;

/**
 * Created by admin on 2017/12/22.
 * <p>
 * 表情包页面
 */

public class CountenanceFragment extends Fragment implements SendListener {
    private View view;
    private RecyclerView recyclerView;
    private ArrayList<String> list = new ArrayList<>();
    private Paint mPaint;
    private PrivateMessageActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_countenance, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //实现点击事件的接口
        CountenanceMessage.getInstance().setSendListener(this);
        initView();
        setRecyclerViewAdapter();
    }

    private void initView() {
        recyclerView = view.findViewById(R.id.fragment_imageselect_list);
        activity = (PrivateMessageActivity) getActivity();
        setPain();
        setItemDecoration(recyclerView);
    }

    private void setRecyclerViewAdapter() {

        //实例化适配器
        CountenanceAdapter expressionAdapter = new CountenanceAdapter(getActivity(), Image.s);
        //获取recyclerview的网格布局管理器
        LinearLayoutManager glm = new GridLayoutManager(getActivity(), 2);

        //设置线性布局为水平布局
        glm.setOrientation(GridLayoutManager.HORIZONTAL);
        //设置布局管理器
        recyclerView.setLayoutManager(glm);
        //给recyclerView设置适配器
        recyclerView.setAdapter(expressionAdapter);
    }

    //设置画笔
    private void setPain() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置颜色
        mPaint.setColor(Color.WHITE);
        //设置填充/
        mPaint.setStyle(Paint.Style.FILL);
    }

    //设置网格
    private void setItemDecoration(RecyclerView recyclerView1) {
        RecyclerView.ItemDecoration d = new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
                //绘制分割线 方法
                drawLine(c, parent);
                draHing(c, parent);
            }
        };
        recyclerView1.addItemDecoration(d);
    }

    //竖线
    private void drawLine(Canvas c, RecyclerView parent) {
        //拿到 开始绘制的顶点 == 分割线的顶点 == 内容的顶点
        final int top = parent.getPaddingTop();
        //拿到 开始绘制的终点
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        //拿到 item个数
        final int childSize = parent.getChildCount();
        //遍历item个数 给每个item画出分割线
        for (int i = 0; i < childSize; i++) {
            //拿到 此下标的布局 == item的布局
            final View child = parent.getChildAt(i);
            //拿到 布局属性
            RecyclerView.LayoutParams layoutParams =
                    (RecyclerView.LayoutParams) child.getLayoutParams();
            //拿到 布局的右坐标 加上 rightMargin == 分割线的左坐标
            final int left = child.getRight() + layoutParams.rightMargin;
            //分割线的右坐标
            final int right = left + 1;
            //绘制
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    //横线
    private void draHing(Canvas c, RecyclerView parent) {
        final int right = parent.getMeasuredWidth();
        final int left = parent.getPaddingLeft();
        final int number = parent.getChildCount();
        for (int j = 0; j < number; j++) {
            final View childAt = parent.getChildAt(j);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) childAt.getLayoutParams();
            final int top = childAt.getBottom() + layoutParams.rightMargin;
            final int bottom = top + 10;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }

    @Override
    public void setItemOnclistener(int i) {
        String s = Image.str[i];
        activity.getEdit(s);
    }
}
