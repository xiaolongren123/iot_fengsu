package com.xiaolongrenzz.xyz.fengsubiao.fragment.page5;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.king.app.updater.AppUpdater;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.xiaolongrenzz.xyz.fengsubiao.PasswordActivity;
import com.xiaolongrenzz.xyz.fengsubiao.R;
import com.xiaolongrenzz.xyz.fengsubiao.userdatabase.UserDao;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Page5Fragment extends Fragment {

    //修改昵称、密码
    private static String getUserName;
    private TextView userName;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private UserDao userDao;

    //升级相关
    private static String updateSign = "0";
    private static String updateVersion = "";
    private static String updateUrl = "";
    //请求权限
    private final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page5, container, false);

        Button useInformation = view.findViewById(R.id.use_information);
        Button checkUpdate = view.findViewById(R.id.check_update);
        userName=view.findViewById(R.id.show_user_name);
        Button changeUserName = view.findViewById(R.id.change_name);
        Button changePassword = view.findViewById(R.id.change_password);

        //显示用户名
        pref = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        getUserName=pref.getString("name", "");
        userName.setText(getUserName);

        sendRequest();

        //修改用户名
        changeUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new XPopup.Builder(getContext()).asInputConfirm("提示", "请输入新的昵称",
                                new OnInputConfirmListener() {
                                    @Override
                                    public void onConfirm(String text) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                userDao = new UserDao();
                                                userDao.updateUser(text,getUserName);
                                                editor = pref.edit();
                                                editor.putString("name", text);
                                                }
                                        }).start();
                                        userName.setText(text);
                                    }
                                })
                        .show();

            }
        });
        //修改密码
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), PasswordActivity.class);
                startActivity(intent);
            }
        });

        //说明
        useInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new XPopup.Builder(getContext()).asConfirm("使用说明",
                                "本APP主要使用于风速信息查看\n" +
                                        "可以实时查看风速数据并图表显示\n" +
                                        "使用前需要在Onenet平台创建产品并添加设备，创建相关Topic\n" +
                                        "在软件对应位置添加相关信息，即可正常查看信息和发送指令",
                                new OnConfirmListener() {
                                    @Override
                                    public void onConfirm() {

                                    }
                                })
                        .show();
            }
        });

        //升级
        checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
                sendRequest();
                if (Objects.equals(updateSign, "0")) {
                    new XPopup.Builder(getContext()).asConfirm("提示",
                                    "当前为最新版本，版本号：" + updateVersion,
                                    new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {

                                        }
                                    })
                            .show();
                } else {
                    new XPopup.Builder(getContext()).asConfirm("提示",
                                    "检测到最新版本，版本号：" + updateVersion + "\n是否立即升级",
                                    new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {
                                            new AppUpdater(requireActivity(),updateUrl).start();
                                        }
                                    })
                            .show();
                }
            }
        });

        return view;
    }

    /**
     * 升级相关
     */
    private void sendRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("https://gitee.com/xiaolongrenzz/record/raw/master/smartRoomUpdate.json").build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    praseJason(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void praseJason(String jsonData) {
        Gson gson = new Gson();
        JsonRootBean jsonRootBean = gson.fromJson(jsonData, JsonRootBean.class);
        updateSign = jsonRootBean.getUpdate();
        updateVersion = jsonRootBean.getVersion();
        updateUrl = jsonRootBean.getUrl();
    }

    /**
     * 权限管理
     */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i = ContextCompat.checkSelfPermission(requireActivity(), permissions[0]);

            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                startRequestPermission();
            }
        }
    }
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), permissions, 1);
    }
}