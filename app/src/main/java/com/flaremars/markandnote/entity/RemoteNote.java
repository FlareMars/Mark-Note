package com.flaremars.markandnote.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by FlareMars on 2016/12/19
 */
public class RemoteNote extends BmobObject {

    private Long date;
    private String title;
    private String content;
    private Boolean putTopFlag;
    private String userId;
    private Boolean isDeprecated = false;

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getPutTopFlag() {
        return putTopFlag;
    }

    public void setPutTopFlag(Boolean putTopFlag) {
        this.putTopFlag = putTopFlag;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getDeprecated() {
        return isDeprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        isDeprecated = deprecated;
    }
}
