package com.xiaolongrenzz.xyz.fengsubiao.util;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义通用工具类
 */
public class CommonUtils {
    /**
     * 获取当前时间
     * @return
     */
    public static String getDateStrFromNow(){
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }

    /**
     * 从日期时间中获取字符串
     * @param dt
     * @return
     */
    public static String getStringFromDate(Date dt){
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(dt);
    }

    /**
     * 显示消息
     * @param context
     * @param msg
     */
    public static void showMsg(Context context,String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示消息对话框
     * @param context
     * @param msg
     */
    public static void showDialog(Context context,String msg){
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage(msg)
                .setPositiveButton("确定",null)
                .setNegativeButton("取消",null)
                .create().show();
    }
}
