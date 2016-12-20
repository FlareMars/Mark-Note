package com.flaremars.markandnote.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.flaremars.markandnote.R;
import com.flaremars.markandnote.common.ComponentHolder;
import com.flaremars.markandnote.entity.Note;
import com.flaremars.markandnote.event.TodoListItemStatusChangedEvent;
import com.flaremars.markandnote.jsinterface.MarkdownJSInterface;
import com.flaremars.markandnote.storage.NoteStorage;
import com.flaremars.markandnote.widget.MarkdownView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewNoteActivity extends AppCompatActivity {

    private static final String TAG = "ViewNoteActivity";

    public static final String PARAM_NOTE_ID = "noteId";

    private boolean needUpdate = false;
    private Toolbar toolbar;
    private Note noteData;
    private MarkdownView markdownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white);
        }

        markdownView = (MarkdownView) findViewById(R.id.md_content);

        initData();
        initViews();
    }

    private void initData() {
        int noteId = getIntent().getIntExtra(PARAM_NOTE_ID, -1);
        noteData = NoteStorage.getInstance().findById(noteId);
    }

    private void initViews() {

        toolbar.setTitle(noteData.getTitle());

        markdownView.getSettings().setJavaScriptEnabled(true);
        markdownView.addJavascriptInterface(new MarkdownJSInterface(), "md");

        markdownView.loadMarkdown(noteData.getContent(),
                MarkdownView.DEFAULT_MARKDOWN_CSS_FILE, MarkdownView.BASE_MARKDOWN_JS_FILES, true);
    }

    // todo 性能优化点
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onTodoItemChanged(TodoListItemStatusChangedEvent event) {
        needUpdate = true;
        long startTime = System.currentTimeMillis();

        List<String> contentArray = new ArrayList<>();
        Collections.addAll(contentArray, noteData.getContent().split("\n"));

        int position = -1;
        String targetStr = event.getContent();
        for (int i = 0, size = contentArray.size();i < size;i++) {
            String content = contentArray.get(i);
            if (content.contains(targetStr)) {
                int spaceIndex = content.indexOf(' ');
                if (spaceIndex > 0) {
                    if (content.substring(spaceIndex+1).trim().equals(targetStr)) {
                        position = i;
                        break;
                    }
                }
            }
        }

        if (position >= 0) {
            contentArray.set(position, (event.isChecked() ? "- " : "+ ") + event.getContent());
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String s : contentArray) {
            stringBuilder.append(s);
            stringBuilder.append("\n");
        }
        noteData.setContent(stringBuilder.toString());

        Log.d(TAG, "cost time = " + (System.currentTimeMillis() - startTime) + "ms");
    }

    @Override
    public void onBackPressed() {
        if (markdownView.canGoBack() && markdownView.getBackListCount() > 0) {
            markdownView.goBack();
            if (markdownView.getBackListCount() == 0) { // 证明回到了markdown初始页，重载一次md
                markdownView.loadMarkdown(noteData.getContent(),
                        MarkdownView.DEFAULT_MARKDOWN_CSS_FILE, MarkdownView.BASE_MARKDOWN_JS_FILES, true);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ComponentHolder.getAppComponent().getEventBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ComponentHolder.getAppComponent().getEventBus().unregister(this);
        if (needUpdate) {
            NoteStorage.getInstance().update(noteData);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
        }
        return true;
    }
}
