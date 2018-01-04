package com.example.admin.nantuoappdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import fm.jiecao.jcvideoplayer_lib.JCUserAction;
import fm.jiecao.jcvideoplayer_lib.JCUserActionStandard;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * 视频详情页  TODO
 */
public class VideoPlayActivity extends RxActivity {
    @Bind(R.id.jc_video)
    JCVideoPlayerStandard mJcVideoPlayerStandard;
    @Bind(R.id.viewpager)
    ViewPager viewPager;

    final int[] ids = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e};
    private List<ImageView> imglist;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_video_play;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        CharSequence titleLable = "南拓";
        setTitle(titleLable);

        initView();
        initDate();
        initViewPager();
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % imglist.size());

        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        actionBar();
    }

    @Override
    protected void loadData() {

    }

    private void initView() {
        //TODO 所给路径
        mJcVideoPlayerStandard.setUp("http://flashmedia.eastday.com/newdate/news/2016-11/shznews1125-19.mp4"
                , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "景点视频");

        JCVideoPlayer.setJcUserAction(new MyUserActionStandard());

    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        //移除当前Activity
        removeCurrentActivity();
        super.onBackPressed();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        // 如果是返回键
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            //want to do
//            Intent intent = new Intent();
//            intent.setClass(VideoPlayActivity.this, MainActivity.class);
//            startActivity(intent);
//            VideoPlayActivity.this.finish();
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    class MyUserActionStandard implements JCUserActionStandard {

        @Override
        public void onEvent(int type, String url, int screen, Object... objects) {
            switch (type) {
                case JCUserAction.ON_CLICK_START_ICON:
                    Log.i("USER_EVENT", "ON_CLICK_START_ICON" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_CLICK_START_ERROR:
                    Log.i("USER_EVENT", "ON_CLICK_START_ERROR" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_CLICK_START_AUTO_COMPLETE:
                    Log.i("USER_EVENT", "ON_CLICK_START_AUTO_COMPLETE" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_CLICK_PAUSE:
                    Log.i("USER_EVENT", "ON_CLICK_PAUSE" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_CLICK_RESUME:
                    Log.i("USER_EVENT", "ON_CLICK_RESUME" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_SEEK_POSITION:
                    Log.i("USER_EVENT", "ON_SEEK_POSITION" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_AUTO_COMPLETE:
                    Log.i("USER_EVENT", "ON_AUTO_COMPLETE" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_ENTER_FULLSCREEN:
                    Log.i("USER_EVENT", "ON_ENTER_FULLSCREEN" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_QUIT_FULLSCREEN:
                    Log.i("USER_EVENT", "ON_QUIT_FULLSCREEN" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_ENTER_TINYSCREEN:
                    Log.i("USER_EVENT", "ON_ENTER_TINYSCREEN" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_QUIT_TINYSCREEN:
                    Log.i("USER_EVENT", "ON_QUIT_TINYSCREEN" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_TOUCH_SCREEN_SEEK_VOLUME:
                    Log.i("USER_EVENT", "ON_TOUCH_SCREEN_SEEK_VOLUME" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserAction.ON_TOUCH_SCREEN_SEEK_POSITION:
                    Log.i("USER_EVENT", "ON_TOUCH_SCREEN_SEEK_POSITION" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;

                case JCUserActionStandard.ON_CLICK_START_THUMB:
                    Log.i("USER_EVENT", "ON_CLICK_START_THUMB" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                case JCUserActionStandard.ON_CLICK_BLANK:
                    Log.i("USER_EVENT", "ON_CLICK_BLANK" + " title is : " + (objects.length == 0 ? "" : objects[0]) + " url is : " + url + " screen is : " + screen);
                    break;
                default:
                    Log.i("USER_EVENT", "unknow");
                    break;
            }
        }
    }


    private void initDate() {
        imglist = new ArrayList<>();
        //将图片资源设置给imageview
        for (int i = 0; i < ids.length; i++) {

            //view视图需要的布局参数
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(10, 10);

            ImageView iv = new ImageView(this);
            iv.setImageResource(ids[i]);
            //将设置好图片的imageview添加入集合中
            imglist.add(iv);
        }
    }

    private void initViewPager() {

        viewPager.setAdapter(new PagerAdapter() {

            /**
             * 判断instantiateItem返回的视图和当前页面是否一致
             * arg0   当前页面
             * arg1 instantiateItem返回的视图
             */
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                //判断页面是否匹配
                return arg0 == arg1;
            }

            /**
             * 设置viewpager需要加载视图的条目
             */
            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            /**
             * 设置要移除哪一张图片
             * container 添加视图的容器
             * position 要移除图片的位置
             * object 要移除的图片资源
             */
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                //调用删除方法直接将当前的imageview返回并删除
                container.removeView((View) object);
            }

            /**
             * 相当于listview中的getview()方法，设置将要加载的图片
             * container 容器 用来存放要加载的图片资源
             * position 要加载的图片位置（下标）指viewpager本身
             */
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                //判断当前的下标余数，避免数组越界
                ImageView imageView = imglist.get(position % imglist.size());
                //将图片资源添加入viewpager中
                container.addView(imageView);
                //将需要加载的图片返回
                return imageView;
            }
        });
    }

    //返回键
    private void actionBar() {
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("南拓");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                removeCurrentActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
