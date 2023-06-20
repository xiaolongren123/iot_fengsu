package com.xiaolongrenzz.xyz.fengsubiao;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.xiaolongrenzz.xyz.fengsubiao.userdatabase.UserDao;

public class PasswordActivity extends AppCompatActivity {

    private EditText oldPass, pass1,pass2;
    private Button confirm;
    private ImageView backToMain;
    private UserDao userDao=new UserDao();

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static String getUserName;
    private static String getUserPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        initView();

        //获取用户名密码
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        getUserName= pref.getString("name", "");
        getUserPassword=pref.getString("password","");

        //退出
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //确认修改
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword=oldPass.getText().toString();
                String password1=pass1.getText().toString();
                String password2=pass2.getText().toString();
                if(password1.equals("")||password2.equals("")||oldPassword.equals("")){
                    new XPopup.Builder(PasswordActivity.this).
                            asConfirm("提示", "新密码或旧密码不能为空",
                                    new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {

                                        }
                                    })
                            .show();
                }else{
                    if (oldPassword.equals(getUserPassword)){
                        if(password1.equals(password2)){
                            userDao.updatePassword(password1,getUserName);
                            Toast.makeText(PasswordActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        new XPopup.Builder(PasswordActivity.this).
                                asConfirm("提示", "旧密码错误",
                                        new OnConfirmListener() {
                                            @Override
                                            public void onConfirm() {

                                            }
                                        })
                                .show();
                    }
                }

            }
        });

    }
    //初始化控件
    private void initView() {
        oldPass=findViewById(R.id.enroll_old_password);
        pass1=findViewById(R.id.enroll_new_password1);
        pass2=findViewById(R.id.enroll_new_password2);
        backToMain=findViewById(R.id.enroll_back_main);
        confirm=findViewById(R.id.confirm);
    }
}