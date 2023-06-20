package com.xiaolongrenzz.xyz.fengsubiao;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.xiaolongrenzz.xyz.fengsubiao.userdatabase.UserDao;
import com.xiaolongrenzz.xyz.fengsubiao.userdatabase.UserInformation;
import com.xiaolongrenzz.xyz.fengsubiao.util.CommonUtils;

public class EnrollActivity extends AppCompatActivity {

    private EditText name, pass1;
    private Button enroll;
    private ImageView back;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);

        initView();
        //退出
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //注册
        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uName = name.getText().toString();
                String uPass1 = pass1.getText().toString();
                if (!uName.isEmpty() & !uPass1.isEmpty()) {

                    CommonUtils.showMsg(EnrollActivity.this, "注册成功");
                    final UserInformation item = new UserInformation();
                    item.setUserName(uName);
                    item.setUserPassword(uPass1);
                    item.setCreateDate(CommonUtils.getDateStrFromNow());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final int iRow = userDao.Enroll(item);
                            finish();
                        }
                    }).start();

                } else {
                    new XPopup.Builder(EnrollActivity.this).
                            asConfirm("提示", "账号密码不能为空",
                                    new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {

                                        }
                                    })
                            .show();
                }
            }
        });
    }

    private void initView() {
        name = findViewById(R.id.enroll_name);
        pass1 = findViewById(R.id.enroll_password);
        back = findViewById(R.id.enroll_back);
        enroll = findViewById(R.id.enroll);
        userDao = new UserDao();
        Handler mainHandler = new Handler(getMainLooper());
    }
}