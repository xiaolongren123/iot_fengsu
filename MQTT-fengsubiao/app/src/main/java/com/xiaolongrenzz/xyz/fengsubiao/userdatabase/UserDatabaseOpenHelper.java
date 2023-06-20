package com.xiaolongrenzz.xyz.fengsubiao.userdatabase;
/**
 * 连接辅助类
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class UserDatabaseOpenHelper {
    private static final String cls = "com.mysql.jdbc.Driver";
    private static final String url = "jdbc:mysql://124.223.33.62:3306/record_user";
    private static final String user = "user";
    private static final String pwd = "123456";

    public static Connection conn;//连接对象
    public static Statement stat;//命令集
    public static PreparedStatement pStat;//预编译命令集
    public static ResultSet rs;//结果集

    public static void getConnection(){
        try {
            Class.forName(cls);
            conn = DriverManager.getConnection(url,user,pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeAll(){
        try {
            if(rs!=null){
                rs.close();
                rs=null;
            }
            if(stat!=null){
                stat.close();
                stat=null;
            }
            if(pStat!=null){
                pStat.close();
                pStat=null;
            }
            if(conn!=null){
                conn.close();
                conn=null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
