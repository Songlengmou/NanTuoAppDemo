package com.example.admin.nantuoappdemo.activity.social;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;

import butterknife.Bind;

/**
 * 个人聊天所发视频页
 */
public class VideoViewActivity extends RxActivity {
    @Bind(R.id.videoView)
    VideoView videoView;
    private String leftPath, rightPath;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_video_view;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        getData();
        initView();
    }

    @Override
    protected void loadData() {

    }

    private void initView() {
        if (TextUtils.isEmpty(rightPath)) {
            videoView.setVideoPath(leftPath);
        } else {
            videoView.setVideoPath(rightPath);
        }

        videoView.start();
        //下面这两个的效果是 播放视频时显示播放进度
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
    }

    public void getData() {
        leftPath = getIntent().getStringExtra("leftPath");//接收方
        rightPath = getIntent().getStringExtra("rightPath");//发送方
    }
}
