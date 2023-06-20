package com.xiaolongrenzz.xyz.fengsubiao.fragment.page2;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.xiaolongrenzz.xyz.fengsubiao.R;
import com.xiaolongrenzz.xyz.fengsubiao.roomdatabase.DeviceData;
import com.xiaolongrenzz.xyz.fengsubiao.roomdatabase.DeviceDataDao;
import com.xiaolongrenzz.xyz.fengsubiao.roomdatabase.DeviceDataDatabase;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Page2Fragment extends Fragment {


    private ColorPickView myColor;
    private TextView colorMessage, showColor;
    private static final String ApiKey = "PAExwY0T6jDcdc1EW4YP70zGcw4=";//API KEY


    private EditText deviceMessage, topicMessage, selectDevice, selectTopic, writeMessage;

    //数据库部分
    private DeviceDataDao deviceDataDao;


    private String deviceForTopic = "";
    private String topicForMessage = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page2, container, false);

        deviceMessage = view.findViewById(R.id.write_device);
        topicMessage = view.findViewById(R.id.write_topic);
        selectDevice = view.findViewById(R.id.select_device);
        selectTopic = view.findViewById(R.id.select_topic);
        Button addMessage = view.findViewById(R.id.add_message);
        writeMessage = view.findViewById(R.id.write_message);
        Button sendMessage = view.findViewById(R.id.send_message);

        ImageView selectUpDevice = view.findViewById(R.id.select_device_image);
        ImageView selectUpTopic = view.findViewById(R.id.select_topic_image);


        //数据库部分
        DeviceDataDatabase deviceDataDatabase = Room.databaseBuilder(requireActivity(), DeviceDataDatabase.class, "device_data_database")
                .allowMainThreadQueries()
                .build();
        deviceDataDao = deviceDataDatabase.getDeviceDataDao();

        //添加主题
        addMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceId = deviceMessage.getText().toString();
                String topic = topicMessage.getText().toString();
                if (deviceId.equals("") || topic.equals("")) {
                    new XPopup.Builder(getContext()).asConfirm("提示", "请输入完整信息",
                                    new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {

                                        }
                                    })
                            .show();
                } else {
                    new XPopup.Builder(getContext()).asConfirm("提示", "是否确认添加\n设备：" + deviceId + "\nTopic：" + topic,
                                    new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {
                                            DeviceData deviceData = new DeviceData(deviceId, topic);
                                            deviceDataDao.insertDeviceData(deviceData);
                                        }
                                    })
                            .show();
                }
            }
        });


        //获取device列表
        final XPopup.Builder builder1 = new XPopup.Builder(getContext())
                .watchView(selectUpDevice);
        selectUpDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] deviceList;
                deviceList = deviceDataDao.getDeviceId().toArray(new String[0]);
                if (deviceDataDao.getCountDeviceData() == 0) {
                    new XPopup.Builder(getContext()).asConfirm("提示", "请先添加设备",
                                    new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {

                                        }
                                    })
                            .show();
                } else {
                    builder1.asAttachList(deviceList, null,
                                    new OnSelectListener() {
                                        @Override
                                        public void onSelect(int position, String text) {
                                            deviceForTopic = text;
                                            Toast.makeText(requireActivity(), "你选择的设备号为：" + text, Toast.LENGTH_SHORT).show();
                                            selectDevice.setText(text);
                                        }
                                    })
                            .show();

                }
            }
        });
        //获取topic列表
        final XPopup.Builder builder2 = new XPopup.Builder(getContext())
                .watchView(selectUpTopic);
        selectUpTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] topicList;
                topicList = deviceDataDao.getTopic(deviceForTopic).toArray(new String[0]);
                if (selectDevice.getText().toString().equals("")) {
                    new XPopup.Builder(getContext()).asConfirm("提示", "请先添加设备",
                                    new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {

                                        }
                                    })
                            .show();
                } else {
                    builder2.asAttachList(topicList, null, new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    topicForMessage = text;
                                    Toast.makeText(requireActivity(), "你选择的Topic为：" + text, Toast.LENGTH_SHORT).show();
                                    selectTopic.setText(text);
                                }
                            })
                            .show();
                }
            }
        });

        //下发指令
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String device=selectDevice.getText().toString();
                String topic=selectTopic.getText().toString();
                String message=writeMessage.getText().toString();
                if (device.equals("") || topic.equals("")){
                    new XPopup.Builder(getContext()).asConfirm("提示", "请先选择设备后再发送指令",
                                    new OnConfirmListener() {
                                        @Override
                                        public void onConfirm() {

                                        }
                                    })
                            .show();
                }else{
                    postMessage(message);
                }
            }
        });
        return view;
    }

    /**
     * 发送命令
     */
    public void postMessage(String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String update=message;
                    //下发命令
                    OkHttpClient client = new OkHttpClient();
                    //数据格式从官方文档看，type用5情况
                    //发送type写法
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), update);
                    Request request = new Request.Builder().url("http://api.heclouds.com/cmds?device_id="+ deviceForTopic).post(requestBody).header("api-key", ApiKey).build();
                    Response response = client.newCall(request).execute();
                } catch (Exception e) {
                }
            }
        }).start();
    }

}