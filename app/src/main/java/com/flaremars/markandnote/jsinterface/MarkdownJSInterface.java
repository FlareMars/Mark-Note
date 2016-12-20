package com.flaremars.markandnote.jsinterface;

import android.webkit.JavascriptInterface;

import com.flaremars.markandnote.common.ComponentHolder;
import com.flaremars.markandnote.event.TodoListItemStatusChangedEvent;

/**
 * Created by FlareMars on 2016/11/15.
 */
public class MarkdownJSInterface {

    public MarkdownJSInterface() {

    }

    @JavascriptInterface
    public void todoListItemStatusChanged(String id, String content, boolean checked) {
        ComponentHolder.getAppComponent().getEventBus().post(new TodoListItemStatusChangedEvent(id, content, checked));
    }
}
