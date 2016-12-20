package com.flaremars.markandnote.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by FlareMars on 2016/12/14
 */
public class User extends BmobObject {

    private String phone;
    private String password;
    private String username;
    private String avatarUrl;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
