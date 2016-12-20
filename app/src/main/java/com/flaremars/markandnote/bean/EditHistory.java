package com.flaremars.markandnote.bean;

/**
 * Created by FlareMars on 2016/11/13.
 */
public class EditHistory {

    private String content;
    private int count;
    private int start;
    private int after;

    public EditHistory() {
        this.content = "";
        this.count = 0;
        this.start = -1;
        this.after = -1;
    }

    public EditHistory(String content, int count, int start, int after) {
        this.content = content;
        this.count = count;
        this.start = start;
        this.after = after;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getAfter() {
        return after;
    }

    public void setAfter(int after) {
        this.after = after;
    }
}
