package com.example.admin.nantuoappdemo.activity.social;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;

import butterknife.Bind;

//图片点击一下就放大到全屏      (目前不完善，这个只能使最后一个图片放大，其他图片和它一样)
//(这个 与 个人聊天适配器 相调用)    TODO 图片放大
public class PictureBigActivity extends RxActivity {
    @Bind(R.id.im_view)
    ImageView im_view;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_picture_big;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String ss = intent.getStringExtra("ss");
        Glide.with(this).load(ss).override(300, 200).
                into(im_view);
    }

    @Override
    protected void loadData() {

    }
}
