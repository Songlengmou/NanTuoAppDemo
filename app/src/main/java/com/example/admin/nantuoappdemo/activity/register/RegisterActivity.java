package com.example.admin.nantuoappdemo.activity.register;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.login.LoginActivity;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;
import com.hyphenate.chat.EMClient;

import butterknife.Bind;

/**
 * 注册页
 */
public class RegisterActivity extends RxActivity implements View.OnClickListener {
    @Bind(R.id.register_username)
    EditText userName;
    @Bind(R.id.register_passwords)
    EditText passwords;
    @Bind(R.id.register_password)
    EditText password;
    @Bind(R.id.register_button)
    Button button;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_register;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        button.setOnClickListener(this);

        actionBar();
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onClick(View v) {
        final String strName = userName.getText().toString();
        final String strPassw = passwords.getText().toString();
        final String strPass = password.getText().toString();

        int i = TestusernameAndpassword(strName, strPassw, strPass);
        switch (i) {
            case 0:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.register);
                builder.setMessage(R.string.info_tures);
                //①
                builder.setNegativeButton(R.string.fou, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//                        Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    }
                });
                //②
                builder.setPositiveButton(R.string.is, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
//                                    Thread.sleep(3000);
                                    EMClient.getInstance().createAccount(strName, strPass);
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        Toast.makeText(RegisterActivity.this, R.string.success_register, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
                break;
            //--------------------------------------------------------------
            case 1:
                Toast.makeText(this, R.string.no_user, Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, R.string.no_pass, Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(this, R.string.no_pass, Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(this, R.string.no_up, Toast.LENGTH_SHORT).show();
                break;
            case 5:
                Toast.makeText(this, R.string.short_user, Toast.LENGTH_SHORT).show();
                break;
            case 6:
                Toast.makeText(this, R.string.length_pass, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    //检验注册时用户名和密码
    private int TestusernameAndpassword(String username, String passwords, String password) {
        if (TextUtils.isEmpty(username)) {
            return 1;
        }
        if (TextUtils.isEmpty(passwords)) {
            return 2;
        }
        if (TextUtils.isEmpty(password)) {
            return 3;
        }
        if (!passwords.equals(password)) {
            return 4;
        }
        if (username.length() < 5) {
            Toast.makeText(this, R.string.length, Toast.LENGTH_SHORT).show();
            return 5;
        }
        if (passwords.length() < 6 || passwords.length() > 18) {
            return 6;
        }
        return 0;
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
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
