package com.xiaolongrenzz.xyz.fengsubiao.fragment.page4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.xiaolongrenzz.xyz.fengsubiao.R;
import com.xiaolongrenzz.xyz.fengsubiao.roomdatabase.DeviceDataDao;
import com.xiaolongrenzz.xyz.fengsubiao.roomdatabase.DeviceDataDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xyz.hydrion.onenethelper.Device;
import xyz.hydrion.onenethelper.DeviceDataPoint;


public class Page4Fragment extends Fragment {

    //数据库部分
    private static final String ApiKey = "PAExwY0T6jDcdc1EW4YP70zGcw4=";//API KEY
    private EditText listDeviceId, listTopicId;
    private ImageView selectUpDevice,selectUpTopic;
    private String deviceForTopic = "";
    private String topicForMessage = "";
    private DeviceDataDao deviceDataDao;
    private DeviceDataDatabase deviceDataDatabase;

    private static ArrayList<ListData> allListData;

    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private DataViewModel dataViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page4, container, false);

        /**
         * 数据库部分
         */
        listDeviceId=view.findViewById(R.id.device_list);
        listTopicId=view.findViewById(R.id.topic_list);
        selectUpDevice=view.findViewById(R.id.select_device_image3);
        selectUpTopic=view.findViewById(R.id.select_topic_image3);



        deviceDataDatabase = Room.databaseBuilder(requireActivity(), DeviceDataDatabase.class, "device_data_database")
                .allowMainThreadQueries()
                .build();
        deviceDataDao = deviceDataDatabase.getDeviceDataDao();

        //同page2fragment
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
                                            listDeviceId.setText(text);
                                        }
                                    })
                            .show();
                }
            }
        });

        final XPopup.Builder builder2 = new XPopup.Builder(getContext())
                .watchView(selectUpTopic);

        selectUpTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] topicList;
                topicList = deviceDataDao.getTopic(deviceForTopic).toArray(new String[0]);
                if (listTopicId.getText().toString().equals("")) {
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
                                    listTopicId.setText(text);
                                }
                            })
                            .show();
                }
            }
        });

        if (deviceDataDao.getCountDeviceData() != 0){
            deviceForTopic=deviceDataDao.getDeviceId().get(0);
            topicForMessage=deviceDataDao.getTopic(deviceForTopic).get(0);
            listDeviceId.setText(deviceForTopic);
            listTopicId.setText(topicForMessage);
        }

        /**
         * 列表部分
         */
        dataViewModel=new ViewModelProvider(this).get(DataViewModel.class);

        listAdapter =new ListAdapter();
        recyclerView=view.findViewById(R.id.data_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listAdapter);

        dataViewModel.getAllListData().observe(getViewLifecycleOwner(), new Observer<List<ListData>>() {
            @Override
            public void onChanged(List<ListData> listData) {

                listAdapter.setAllWords(listData);
                listAdapter.notifyDataSetChanged();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Device device = new Device(deviceForTopic,ApiKey);


                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date1 = null;
                Date date2 = null;
                try {
                    date2 = format.parse("2023-07-27 21:41:02");
                    date1 = format.parse("2022-06-12 21:40:48");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int i=0;
                List<DeviceDataPoint> dataPoints = device.getDataPointsBetweenTwoTimes(
                        topicForMessage,date1,date2);

                allListData=new ArrayList<>();

                for (DeviceDataPoint p:dataPoints) {
                    System.out.println(p.getValue() + " " +p.getTime());
                    ListData listData = new ListData(p.getValue(),p.getTime());
                    allListData.add(i,listData);
                }
                dataViewModel.setAllListData(allListData);
            }
        }).start();

        return view;
    }
}