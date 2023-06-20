package com.xiaolongrenzz.xyz.fengsubiao.roomdatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DeviceData.class},version = 1,exportSchema = false)
public abstract class DeviceDataDatabase extends RoomDatabase {
    public abstract DeviceDataDao getDeviceDataDao();
}
