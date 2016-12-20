package com.flaremars.markandnote.event;

/**
 * Created by FlareMars on 2016/12/13.
 */
public class PutTopNoteEvent {

    private Integer localId;
    private String noteId;
    private Boolean top;

    public PutTopNoteEvent(Integer localId, String noteId, Boolean top) {
        this.localId = localId;
        this.noteId = noteId;
        this.top = top;
    }

    public PutTopNoteEvent() {

    }

    public Integer getLocalId() {
        return localId;
    }

    public void setLocalId(Integer localId) {
        this.localId = localId;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public Boolean getTop() {
        return top;
    }

    public void setTop(Boolean top) {
        this.top = top;
    }
}
