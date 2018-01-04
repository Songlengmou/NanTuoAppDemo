package com.example.admin.nantuoappdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;
import com.example.admin.nantuoappdemo.rx.RenZhen_SP;

/**
 * 向上滑动解锁页
 */
public class TouchUpActivity extends RxActivity {
    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_touch_up;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        CharSequence titleLable = "南拓";
        setTitle(titleLable);
    }

    @Override
    protected void loadData() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
            if (y1 - y2 > 50) {
                Boolean bol = RenZhen_SP.getBoolean(TouchUpActivity.this, "key", false);
                if (bol == true) {
                    startActivity(new Intent(TouchUpActivity.this, MainActivity.class));
                    finish();
                    RenZhen_SP.putBoolean(TouchUpActivity.this, "key", false);
                } else {
//                    Toast.makeText(TouchUpActivity.this, R.string.up, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TouchUpActivity.this, LanguageSelectionActivity.class));
                    finish();
                    RenZhen_SP.putBoolean(TouchUpActivity.this, "key", true);
                }
            } else if (y2 - y1 > 50) {
                Toast.makeText(TouchUpActivity.this, R.string.down, Toast.LENGTH_SHORT).show();
                finish();
            } else if (x1 - x2 > 50) {
                Toast.makeText(TouchUpActivity.this, R.string.left, Toast.LENGTH_SHORT).show();
                finish();
            } else if (x2 - x1 > 50) {
                Toast.makeText(TouchUpActivity.this, R.string.right, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        return super.onTouchEvent(event);
    }
}
