package com.flaremars.markandnote.event;

/**
 * Created by FlareMars on 2016/11/10.
 */
public class TodoListItemStatusChangedEvent {

    private String id;
    private String content;
    private boolean checked;

    public TodoListItemStatusChangedEvent(String id, String content, boolean checked) {
        this.id = id;
        this.content = content;
        this.checked = checked;
    }

    public int linePosition() {
        int position = -1;
        if (id != null) {
            int index = id.indexOf('_');
            if (index > 0 && index < (id.length() - 1)) {
                position = Integer.valueOf(id.substring(index+1));
            }
        }
        return position;
    }

    public boolean isValid() {
        return id != null && id.indexOf('_') > 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
