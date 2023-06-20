package com.xiaolongrenzz.xyz.fengsubiao.roomdatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DeviceData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "device_id")
    private String deviceId;
    @ColumnInfo(name = "topic")
    private String topic;


    public DeviceData(String deviceId, String topic) {
        this.deviceId = deviceId;
        this.topic = topic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
