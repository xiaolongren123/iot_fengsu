package com.xiaolongrenzz.xyz.fengsubiao.userdatabase;

import java.io.Serializable;

public class UserInformation implements Serializable {
    private int id;
    private String userName;
    private String userPassword;
    private String createDate;

    public UserInformation() {

    }

    public UserInformation(int id, String uname, String userPassword, String creatDate) {
        this.id = id;
        this.userName = uname;
        this.userPassword = userPassword;
        this.createDate = creatDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUname() {
        return userName;
    }

    public void setUserName(String uname) {
        this.userName = uname;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
