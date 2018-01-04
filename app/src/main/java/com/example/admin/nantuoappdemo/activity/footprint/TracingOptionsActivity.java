package com.example.admin.nantuoappdemo.activity.footprint;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.trace.model.LocationMode;
import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.ChooseMapFunctionActivity;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;
import com.example.admin.nantuoappdemo.utils.Constants;

import butterknife.Bind;

import static com.baidu.trace.model.LocationMode.High_Accuracy;

public class TracingOptionsActivity extends RxActivity {
    // 返回结果
    private Intent result = null;

    @Bind(R.id.gather_interval)
    EditText gatherIntervalText = null;
    @Bind(R.id.pack_interval)
    EditText packIntervalText = null;
    @Bind(R.id.location_mode)
    RadioGroup locationModeGroup;
    @Bind(R.id.object_storage)
    RadioGroup needBosGroup;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_tracing_options;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        actionBar();

        gatherIntervalText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                EditText textView = (EditText) view;
                String hintStr = textView.getHint().toString();
                if (hasFocus) {
                    textView.setHint("");
                } else {
                    textView.setHint(hintStr);
                }
            }
        });

        packIntervalText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                EditText textView = (EditText) view;
                String hintStr = textView.getHint().toString();
                if (hasFocus) {
                    textView.setHint("");
                } else {
                    textView.setHint(hintStr);
                }
            }
        });

    }

    @Override
    protected void loadData() {

    }

    public void onCancel(View v) {
        super.onBackPressed();
    }

    public void onFinish(View v) {
        result = new Intent();

        RadioButton locationModeRadio = findViewById(locationModeGroup.getCheckedRadioButtonId());
        LocationMode locationMode = High_Accuracy;
        switch (locationModeRadio.getId()) {
            case R.id.device_sensors:
                locationMode = LocationMode.Device_Sensors;
                break;

            case R.id.battery_saving:
                locationMode = LocationMode.Battery_Saving;
                break;

            case R.id.high_accuracy:
                locationMode = High_Accuracy;
                break;

            default:
                break;
        }
        result.putExtra("locationMode", locationMode.name());


        RadioButton needBosRadio = findViewById(needBosGroup.getCheckedRadioButtonId());
        boolean isNeedObjectStorage = false;
        switch (needBosRadio.getId()) {
            case R.id.close_bos:
                isNeedObjectStorage = false;
                break;

            case R.id.open_bos:
                isNeedObjectStorage = true;
                break;

            default:
                break;
        }
        result.putExtra("isNeedObjectStorage", isNeedObjectStorage);

        EditText gatherIntervalText = findViewById(R.id.gather_interval);
        EditText packIntervalText = findViewById(R.id.pack_interval);
        String gatherIntervalStr = gatherIntervalText.getText().toString();
        String packIntervalStr = packIntervalText.getText().toString();

        if (!TextUtils.isEmpty(gatherIntervalStr)) {
            try {
                result.putExtra("gatherInterval", Integer.parseInt(gatherIntervalStr));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(packIntervalStr)) {
            try {
                result.putExtra("packInterval", Integer.parseInt(packIntervalStr));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        //        RadioGroup supplementModeGroup = (RadioGroup) findViewById(R.id.supplement_mode);
        //        RadioButton supplementModeOptionBtn = (RadioButton) findViewById(supplementModeGroup
        // .getCheckedRadioButtonId());
        //        SupplementMode supplementMode = SupplementMode.no_supplement;
        //        switch (supplementModeOptionBtn.getId()) {
        //            case R.id.no_supplement:
        //                supplementMode = SupplementMode.no_supplement;
        //                break;
        //
        //            case R.id.straight:
        //                supplementMode = SupplementMode.straight;
        //                break;
        //
        //            case R.id.walking:
        //                supplementMode = SupplementMode.walking;
        //                break;
        //
        //            case R.id.riding:
        //                supplementMode = SupplementMode.riding;
        //                break;
        //
        //            case R.id.driving:
        //                supplementMode = SupplementMode.driving;
        //                break;
        //
        //            default:
        //                break;
        //        }
        //        result.putExtra("supplementMode", supplementMode.name());

        setResult(Constants.RESULT_CODE, result);
        super.onBackPressed();
    }

    //返回键
    private void actionBar() {
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.app_title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(TracingOptionsActivity.this, TracingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
