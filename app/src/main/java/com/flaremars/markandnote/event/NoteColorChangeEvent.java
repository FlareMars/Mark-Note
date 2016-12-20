package com.flaremars.markandnote.event;

/**
 * Created by FlareMars on 2016/11/14.
 */
public class NoteColorChangeEvent {

    private int color;

    public NoteColorChangeEvent(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
