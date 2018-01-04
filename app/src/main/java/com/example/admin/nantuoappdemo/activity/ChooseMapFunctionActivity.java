package com.example.admin.nantuoappdemo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.arnavigation.ARNavigationActivity;
import com.example.admin.nantuoappdemo.activity.aroundlife.PoiSearchActivity;
import com.example.admin.nantuoappdemo.activity.footprint.TraceMainActivity;
import com.example.admin.nantuoappdemo.activity.login.LoginActivity;
import com.example.admin.nantuoappdemo.activity.offline.OfflineMainActivity;
import com.example.admin.nantuoappdemo.activity.route.RoutePlanActivity;
import com.example.admin.nantuoappdemo.activity.voice.NaviActivity;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;

/**
 * 百度地图涉及到的功能选择
 */
public class ChooseMapFunctionActivity extends RxActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_choose_map_function;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        actionBar();
    }

    @Override
    protected void loadData() {

    }

    public void enterButtonProcess(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.route:
                intent = new Intent(ChooseMapFunctionActivity.this, RoutePlanActivity.class);
                startActivity(intent);
                break;
            case R.id.ARnav:
                intent = new Intent(ChooseMapFunctionActivity.this, ARNavigationActivity.class);
                startActivity(intent);
                break;
            case R.id.around:
                intent = new Intent(ChooseMapFunctionActivity.this, PoiSearchActivity.class);
                startActivity(intent);
                break;
            case R.id.eagle:
                intent = new Intent(ChooseMapFunctionActivity.this, TraceMainActivity.class);
                startActivity(intent);
                break;
            case R.id.voice:
                intent = new Intent(ChooseMapFunctionActivity.this, NaviActivity.class);
                startActivity(intent);
                break;
            case R.id.offline:
                intent = new Intent(ChooseMapFunctionActivity.this, OfflineMainActivity.class);
                startActivity(intent);
                break;
            case R.id.chat:
                new MaterialDialog.Builder(ChooseMapFunctionActivity.this)
                        .content(R.string.choose)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.no)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                startActivity(new Intent(ChooseMapFunctionActivity.this, LoginActivity.class));//My聊天室
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
//                Toast.makeText(ChooseMapFunctionActivity.this, "该功能仍在完善中，敬请期待!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tel:
                intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + "010-8500-7428"));  //客服
                startActivity(intent);
                break;
        }
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
                startActivity(new Intent(ChooseMapFunctionActivity.this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
