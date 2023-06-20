package com.xiaolongrenzz.xyz.fengsubiao.roomdatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DeviceDataDao {
    @Insert
    void insertDeviceData(DeviceData...deviceData);

    @Update
    void updateDeviceData(DeviceData...deviceData);

    @Delete
    void deleteDeviceData(DeviceData...deviceData);

    @Query("DELETE FROM DeviceData")
    void deleteAllDeviceData();

    @Query("SELECT * FROM DeviceData ORDER BY id DESC")
    List<DeviceData> getAllDeviceData();

    @Query("SELECT device_id FROM DeviceData ORDER BY id DESC")
    List<String> getDeviceId();

    @Query("SELECT topic FROM DeviceData WHERE device_id=:device ORDER BY id DESC")
    List<String> getTopic(String device);

    @Query("SELECT COUNT(*) FROM DeviceData")
    int getCountDeviceData();

}
