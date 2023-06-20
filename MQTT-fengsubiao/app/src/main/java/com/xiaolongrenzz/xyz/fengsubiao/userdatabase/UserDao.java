package com.xiaolongrenzz.xyz.fengsubiao.userdatabase;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UserDao extends UserDatabaseOpenHelper{
    private static final String TAG = "ccc";

    //查询所有用户信息
    public List<UserInformation>getAllUserList(){
        List<UserInformation> list = new ArrayList<>();
        try {
            getConnection();
            String sql = "select * from user";
            pStat = conn.prepareStatement(sql);
            rs = pStat.executeQuery();
            while(rs.next()){
                UserInformation item = new UserInformation();
                item.setId(rs.getInt("id"));
                item.setUserName(rs.getString("name"));
                item.setUserPassword(rs.getString("password"));
                item.setCreateDate(rs.getString("time"));
                list.add(item);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            closeAll();
        }
        return list;
    }

    public UserInformation getUserData(String uName,String uPassword){
        UserInformation item = null;
        try {
            getConnection();
            String sql = "select * from user where name=? and password=?";
            pStat = conn.prepareStatement(sql);
            pStat.setString(1,uName);
            pStat.setString(2,uPassword);
            rs = pStat.executeQuery();
            if(rs.next()){
                item = new UserInformation();
                item.setId(rs.getInt("id"));
                item.setUserName(uName);
                item.setUserPassword(uPassword);
                item.setCreateDate(rs.getString("time"));
            }
            Log.d(TAG, "getUserData: "+item.getId());
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            closeAll();
        }
        return item;
    }

    public int Enroll(UserInformation item){
        int iRow = 0;
        try {
            getConnection();
            String sql = "insert into user(name,password) values(?, ?)";
            pStat = conn.prepareStatement(sql);
            pStat.setString(1,item.getUname());
            pStat.setString(2,item.getUserPassword());
            iRow = pStat.executeUpdate();
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            closeAll();
        }
        return iRow;
    }
    public int updatePassword(String password,String name){
        int iRow = 0;
        try {
            getConnection();
            String sql = "update user set password=? where name=?";
            pStat = conn.prepareStatement(sql);
            pStat.setString(1,password);
            pStat.setString(2,name);
            iRow = pStat.executeUpdate();
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            closeAll();
        }
        return iRow;
    }

    public void updateUser(String newName,String oldName){
        int iRow = 0;
        try {
            getConnection();
            String sql = "update user set name=? where name=?";
            pStat = conn.prepareStatement(sql);
            pStat.setString(1,newName);
            pStat.setString(2,oldName);
            iRow = pStat.executeUpdate();
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            closeAll();
        }
    }

    public String getUserPassword(String uName){
        UserInformation item = null;
        try {
            getConnection();
            String sql = "select password from user where name=?";
            pStat = conn.prepareStatement(sql);
            pStat.setString(1,uName);
            rs = pStat.executeQuery();
            if(rs.next()){
                item = new UserInformation();
                item.setUserPassword(rs.getString("password"));
            }
            Log.d(TAG, "getUserData: "+item.getId());
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            closeAll();
        }
        return item.getUserPassword();
    }

    public int deleteUser(String name){
        int iRow = 0;
        try {
            getConnection();
            String sql = "delete from user where name=?";
            pStat = conn.prepareStatement(sql);
            pStat.setString(1,name);
            iRow = pStat.executeUpdate();
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            closeAll();
        }
        return iRow;
    }
}
