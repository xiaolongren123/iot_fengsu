package com.xiaolongrenzz.xyz.fengsubiao.fragment.page3;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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


public class Page3Fragment extends Fragment {

    private static final String ApiKey = "PAExwY0T6jDcdc1EW4YP70zGcw4=";//API KEY
    private EditText chartDeviceId, chartTopicId;
    private String deviceForTopic = "";
    private String topicForMessage = "";

    private DeviceDataDao deviceDataDao;

    //图表部分
    private List<Entry> entries = new ArrayList<Entry>();
    LineDataSet lineDataSet;
    private Handler handler;

    private LineChart lineChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page3, container, false);

        chartDeviceId = view.findViewById(R.id.device_chart);
        chartTopicId = view.findViewById(R.id.topic_chart);
        ImageView selectUpDevice = view.findViewById(R.id.select_device_image2);
        ImageView selectUpTopic = view.findViewById(R.id.select_topic_image2);

        lineChart=view.findViewById(R.id.chart);

        //数据库部分
        DeviceDataDatabase deviceDataDatabase = Room.databaseBuilder(requireActivity(), DeviceDataDatabase.class, "device_data_database")
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
                                            chartDeviceId.setText(text);
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
                if (chartDeviceId.getText().toString().equals("")) {
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
                                    chartTopicId.setText(text);
                                }
                            })
                            .show();
                }
            }
        });


        if (deviceDataDao.getCountDeviceData() != 0){
            deviceForTopic=deviceDataDao.getDeviceId().get(0);
            topicForMessage=deviceDataDao.getTopic(deviceForTopic).get(0);
            chartDeviceId.setText(deviceForTopic);
            chartTopicId.setText(topicForMessage);
        }


        //图表部分

        //无数据时显示
        lineChart.setNoDataText("没有获取到数据哦~");
        lineChart.setNoDataTextColor(Color.parseColor("#00bcef"));
        lineChart.setExtraBottomOffset(10f);
        //不显示description
        lineChart.getDescription().setEnabled(false);
        //不显示边界
        lineChart.setDrawBorders(false);
        //不显示图例
        lineChart.getLegend().setEnabled(false);
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.setExtraBottomOffset(10f);
        //获取X轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(5);
        xAxis.setTextSize(12f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setAxisMinimum(0);
        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setEnabled(false);
//这不是同一个项目吧？是，MQTT只是单独拉出来调试
        //将数据赋给数据集,一个数据集表示一条线
        lineDataSet = new LineDataSet(entries,"");
        //线颜色
        lineDataSet.setColor(Color.parseColor("#FFA2A2"));
        //线宽度
        lineDataSet.setLineWidth(2.0f);
        //显示圆点
        lineDataSet.setDrawCircles(true);
        //设置圆点颜色(外圈)
        lineDataSet.setCircleColor(Color.parseColor("#008CFF"));
        //设置圆点填充颜色
        lineDataSet.setCircleHoleColor(Color.parseColor("#008CFF"));
        //设置线条为平滑曲线
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        //设置直线图填充
        lineDataSet.setDrawFilled(true);
        //设置填充颜色
        lineDataSet.setFillColor(Color.parseColor("#FFA2A2"));
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);



        //刷新图表相关
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                lineDataSet.setColor(Color.parseColor("#38C24B"));
                //线宽度
                lineDataSet.setLineWidth(2.0f);
                //显示圆点
                lineDataSet.setDrawCircles(true);
                //设置圆点颜色(外圈)
                lineDataSet.setCircleColor(Color.parseColor("#CCD31D"));
                //设置圆点填充颜色
                lineDataSet.setCircleHoleColor(Color.parseColor("#CCD31D"));
                //设置线条为平滑曲线
                lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

                LineData lineData = new LineData(lineDataSet);
                lineChart.setData(lineData);
                lineChart.invalidate();
            }
        };
        Thread reFresh = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Message message = Message.obtain();
                    message.arg1 = 1;
                    Device device = new Device(deviceForTopic, ApiKey);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date1 = null;
                    Date date2 = null;
                    try {
                        date2 = format.parse("2023-07-27 21:41:02");
                        date1 = format.parse("2022-07-12 21:40:48");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    int i = 0;
                    List<DeviceDataPoint> dataPoints = device.getDataPointsBetweenTwoTimes(
                            topicForMessage, date1, date2);


                    ArrayList<String> setX = new ArrayList<>();
                    List<Entry> entries = new ArrayList<>();


                    for (DeviceDataPoint p : dataPoints) {
                        String input = p.getTime();
                        String output = input.substring(
                                input.indexOf(' ') + 1,
                                input.indexOf('.')
                        );
                        setX.add(i, output);
                        entries.add(new Entry(i, (float) Double.parseDouble(p.getValue().toString())));
                        i += 1;
                    }

                    xAxis.setValueFormatter(new IndexAxisValueFormatter(setX));
                    lineDataSet = new LineDataSet(entries, "");


                    handler.sendMessage(message);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        reFresh.start();

        return view;
    }
}