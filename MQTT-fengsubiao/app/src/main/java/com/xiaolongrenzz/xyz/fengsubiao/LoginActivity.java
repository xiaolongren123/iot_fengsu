package com.xiaolongrenzz.xyz.fengsubiao;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.xiaolongrenzz.xyz.fengsubiao.userdatabase.UserDao;
import com.xiaolongrenzz.xyz.fengsubiao.userdatabase.UserInformation;
import com.xiaolongrenzz.xyz.fengsubiao.util.CommonUtils;


public class LoginActivity extends AppCompatActivity {

    private TextView enroll;
    private EditText userName, userPassword;
    private CheckBox remember;
    private Button login;

    private Handler mainHandler;
    private UserDao userDao;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        final boolean[] result = {false};

        //记住密码
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            String name = pref.getString("name", "");
            String password = pref.getString("password", "");
            userName.setText(name);
            userPassword.setText(password);
            remember.setChecked(true);
        }

        //注册
        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, com.xiaolongrenzz.xyz.fengsubiao.EnrollActivity.class);
                startActivity(intent);
            }
        });

        //登录
        login.setOnClickListener(view -> {
            String name = userName.getText().toString().trim();
            String password = userPassword.getText().toString();
            if (TextUtils.isEmpty(name)) {
                userName.requestFocus();
                CommonUtils.showMsg(LoginActivity.this, "请填写完整信息");
            } else if (TextUtils.isEmpty(password)) {
                userPassword.requestFocus();
                CommonUtils.showMsg(LoginActivity.this, "请填写完整信息");
            } else if (TextUtils.isEmpty(name) && TextUtils.isEmpty(password)) {
                userName.requestFocus();
                CommonUtils.showMsg(LoginActivity.this, "请填写完整信息");
            } else {
                BasePopupView popupView = new XPopup.Builder(this)
                        .asLoading("正在加载")
                        .show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        UserInformation item = userDao.getUserData(name, password);
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (item == null) {
                                    popupView.smartDismiss();
                                    CommonUtils.showMsg(LoginActivity.this, "账号或密码错误");
                                } else {

                                    popupView.smartDismiss();

                                    editor = pref.edit();
                                    if (remember.isChecked()) {
                                        editor.putBoolean("remember_password", true);
                                        editor.putString("name", name);
                                        editor.putString("password", password);
                                    } else {
                                        editor.clear();
                                    }
                                    editor.apply();

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void initView() {
        enroll = findViewById(R.id.enrollNow);
        remember = findViewById(R.id.login_remember);
        userName = findViewById(R.id.login_name);
        userPassword = findViewById(R.id.login_password);
        remember = findViewById(R.id.login_remember);
        login = findViewById(R.id.login);

        userDao = new UserDao();
        mainHandler = new Handler(getMainLooper());

    }

}