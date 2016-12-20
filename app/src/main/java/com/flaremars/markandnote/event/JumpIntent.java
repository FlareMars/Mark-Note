package com.flaremars.markandnote.event;

/**
 * Created by FlareMars on 2016/11/4.
 * 超链接跳转封装Intent
 */
public class JumpIntent {

    private String contentHolder;

    public static JumpIntent fromUrl(String url) {
        JumpIntent intent = new JumpIntent();
        intent.contentHolder = url;
        return intent;
    }

    public String getContentHolder() {
        return contentHolder;
    }

    public void setContentHolder(String contentHolder) {
        this.contentHolder = contentHolder;
    }
}
