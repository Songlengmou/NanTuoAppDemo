package com.example.admin.nantuoappdemo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 语种选择页
 */
public class LanguageSelectionActivity extends RxActivity implements View.OnClickListener {
    @Bind(R.id.spinner)
    Spinner mySpinner;
    @Bind(R.id.textView)
    TextView myTextView;
    @Bind(R.id.btn_ture)
    Button ture;

    private List<String> list = new ArrayList<>();//创建一个String类型的数组列表。
    private ArrayAdapter<String> adapter;//创建一个数组适配器
    SharedPreferences sp;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_language_selection;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        CharSequence titleLable = "南拓";
        setTitle(titleLable);

        sp = getSharedPreferences("sp_demo", Context.MODE_PRIVATE);
        init();
    }

    @Override
    protected void loadData() {

    }

    private void init() {
        // 添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项，即数据源
        list.add("中文");
        list.add("English");
        list.add("일본어");
        list.add("日本語");
        list.add("Deutsch");
        list.add("Français");
        list.add("中文繁体");
        //1.为下拉列表定义一个数组适配器，这个数组适配器就用到里前面定义的list。装的都是list所添加的内容
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);//样式为原安卓里面有的android.R.layout.simple_spinner_item，让这个数组适配器装list内容。
        //2.为适配器设置下拉菜单样式。adapter.setDropDownViewResource
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //3.以上声明完毕后，建立适配器,有关于sipnner这个控件的建立。用到myspinner
        mySpinner.setAdapter(adapter);
        //4.为下拉列表设置各种点击事件，以响应菜单中的文本item被选中了，用setOnItemSelectedListener
        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
            @SuppressLint("ResourceAsColor")
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                Log.e("aaaa", "  " + arg2 + "   " + arg3);
                // TODO Auto-generated method stub
                // 将所选mySpinner 的值带入myTextView 中
                myTextView.setText("您选择的是：" + adapter.getItem(arg2));//文本说明
                myTextView.setTextColor(R.color.colorPrimary);
                Configuration config = getResources().getConfiguration();
                switch (arg2) {
                    case 0:
                        config.locale = Locale.SIMPLIFIED_CHINESE;
                        break;
                    case 1:
                        config.locale = Locale.ENGLISH;
                        break;
                    case 2:
                        config.locale = Locale.KOREAN;
                        break;
                    case 3:
                        config.locale = Locale.JAPAN;
                        break;
                    case 4:
                        config.locale = Locale.GERMANY;
                        break;
                    case 5:
                        config.locale = Locale.FRANCE;
                        break;
                    case 6:
                        config.locale = Locale.TRADITIONAL_CHINESE; //中文繁体
                        break;
                }

                getResources().updateConfiguration(config, getResources().getDisplayMetrics());

                int zw = sp.getInt("zw", 0);
                if (zw != arg2) {
                    recreate();
                    sp.edit().putInt("zw", arg2).commit();
                }
                int yy = sp.getInt("yy", 1);
                if (yy != arg2) {
                    recreate();
                    sp.edit().putInt("yy", arg2).commit();
                }
                int hy = sp.getInt("hy", 2);
                if (hy != arg2) {
                    recreate();
                    sp.edit().putInt("hy", arg2).commit();
                }
                int ry = sp.getInt("ry", 3);
                if (ry != arg2) {
                    recreate();
                    sp.edit().putInt("ry", arg2).commit();
                }
                int dy = sp.getInt("dy", 4);
                if (dy != arg2) {
                    recreate();
                    sp.edit().putInt("dy", arg2).commit();
                }
                int fy = sp.getInt("fy", 5);
                if (fy != arg2) {
                    recreate();
                    sp.edit().putInt("fy", arg2).commit();
                }
                int zf = sp.getInt("zf", 6);
                if (zf != arg2) {
                    recreate();
                    sp.edit().putInt("zf", arg2).commit();
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                myTextView.setText("Nothing");
            }
        });
    }


    @OnClick(R.id.btn_ture)
    @Override
    public void onClick(View v) {
        new MaterialDialog.Builder(LanguageSelectionActivity.this)
                .content(R.string.ture_choose)
                .positiveText(R.string.all_sure)
                .negativeText(R.string.all_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startActivity(new Intent(LanguageSelectionActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
