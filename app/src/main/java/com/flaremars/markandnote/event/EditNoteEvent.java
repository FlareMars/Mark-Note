package com.flaremars.markandnote.event;

/**
 * Created by FlareMars on 2016/12/4.
 */
public class EditNoteEvent {

    private Integer localId;
    private String noteId;

    public EditNoteEvent(Integer localId, String noteId) {
        this.localId = localId;
        this.noteId = noteId;
    }

    public EditNoteEvent() {

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
}
