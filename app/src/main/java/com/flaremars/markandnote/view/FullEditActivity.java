package com.flaremars.markandnote.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.FullScreenDrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.flaremars.markandnote.R;
import com.flaremars.markandnote.common.BaseActivity;
import com.flaremars.markandnote.common.ComponentHolder;
import com.flaremars.markandnote.entity.Note;
import com.flaremars.markandnote.entity.storage.NoteBaseStorage;
import com.flaremars.markandnote.event.NoteColorChangeEvent;
import com.flaremars.markandnote.jsinterface.MarkdownJSInterface;
import com.flaremars.markandnote.service.SynchronousService;
import com.flaremars.markandnote.util.StringUtils;
import com.flaremars.markandnote.widget.EditHistoryLogic;
import com.flaremars.markandnote.widget.MarkdownProcessor;
import com.flaremars.markandnote.widget.MarkdownView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FullEditActivity extends BaseActivity implements FullScreenDrawerLayout.DrawerListener,
                                                                ColorChooserDialog.ColorCallback {

    private static final String TAG = "FullEditActivity";
    public static final String PARAM_TARGET_NOTE_ID = "targetNoteId";
    public static final int REQUEST_CODE_SELECT_PICTURE = 1;

    private FullScreenDrawerLayout previewDrawerLayout;
    private MarkdownView mdPreviewView;
    private EditText titleEt;
    private EditText contentEt;

    private EditHistoryLogic historyLogic;

    // biz content
    private int noteColor;
    private Note noteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setContentView(R.layout.activity_full_edit);

        previewDrawerLayout = (FullScreenDrawerLayout) findViewById(R.id.drawer_layout_preview);
        mdPreviewView = (MarkdownView) previewDrawerLayout.findViewById(R.id.md_preview_content);
        titleEt = (EditText) findViewById(R.id.et_title);
        contentEt = (EditText) findViewById(R.id.editText);
        initViews();
    }

    private void initData() {
        noteColor = getResources().getColor(R.color.action_gray);
        int noteId = getIntent().getIntExtra(PARAM_TARGET_NOTE_ID, -1);
        if (noteId != -1) {
            noteData = NoteBaseStorage.getInstance().findById(noteId);
        } else {
            noteData = new Note(null, "", 0L, "", "", "");
        }

    }

    private void initViews() {
        previewDrawerLayout.addDrawerListener(this);
        historyLogic = new EditHistoryLogic(contentEt);
        mdPreviewView.getSettings().setJavaScriptEnabled(true);
        mdPreviewView.addJavascriptInterface(new MarkdownJSInterface(), "md");

        titleEt.setHint(new SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(new Date()));
        titleEt.setText(noteData.getTitle());
        contentEt.setText(noteData.getContent());

    }

    public void editRedo() {
        historyLogic.redo();
    }

    public void editUndo() {
        historyLogic.undo();
    }

    public void insertLink(String title, String url) {
        if (StringUtils.INSTANCE.isEmpty(title) || StringUtils.INSTANCE.isEmpty(url)) {
            Toast.makeText(this, "标题或链接均不可为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String insertContent = String.format(MarkdownProcessor.FORMAT_LINK, title, url);
        contentEt.append(insertContent);
    }

    public void insertTodoList() {
        contentEt.append("\n");
        contentEt.append(MarkdownProcessor.HOLDER_TODOLIST);
    }

    public void togglePreview() {
        if (previewDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            previewDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            previewDrawerLayout.openDrawer(GravityCompat.END);
        }
    }

    public void editComplete() {
        if (noteData.getDate() == 0L) {
            noteData.setDate(new Date().getTime());
        }

        noteData.setTitle(titleEt.getText().toString());
        if (StringUtils.INSTANCE.isEmpty(noteData.getTitle())) {
            noteData.setTitle(new SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(new Date())); // TODO 加入地理信息
        }
        noteData.setContent(contentEt.getText().toString());

        Pattern pattern = Pattern.compile("!\\[\\S+]\\(\\S+\\)");
        Matcher matcher = pattern.matcher(noteData.getContent());

        List<String> urls = new ArrayList<>();
        while (matcher.find()) {
            String content = matcher.group();
            urls.add(content.substring(content.indexOf("(") + 1, content.length()-1));
            Log.d(TAG, urls.toString());
        }
        noteData.setPictureUrls(urls);

        noteData.setModifiedTime(new Date().getTime());

        NoteBaseStorage.getInstance().saveOrUpdate(noteData);

        synchronizeInBackground();

        this.finish();
    }

    private void synchronizeInBackground() {
        Intent intent = new Intent(this, SynchronousService.class);
        intent.putExtra(SynchronousService.ACTION_INTENT, SynchronousService.ACTION_PUSH);
        startService(intent);
    }

    public void fastInput(View fastInputBtn) {
        if (fastInputBtn instanceof TextView) {
            CharSequence inputtingContent = ((TextView)fastInputBtn).getText();
            int inputIndex = contentEt.getSelectionStart();
            contentEt.getText().insert(inputIndex, inputtingContent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_PICTURE) {
                for (String path : data.getStringExtra("originalData").split(",")) {
                    if (!StringUtils.INSTANCE.isEmpty(path)) {
                        contentEt.append(String.format(MarkdownProcessor.FORMAT_PICTURE_LOCAL, "PICTURE", path));
                    }
                }
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (previewDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            previewDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            String initialContent = noteData.getContent().trim();
            String currentContent = contentEt.getText().toString().trim();
            if (!initialContent.equals(currentContent)) {
                new MaterialDialog.Builder(this)
                        .positiveText(R.string.dialog_msg_save_and_quit)
                        .positiveColor(Color.GREEN)
                        .negativeText(R.string.cancel)
                        .negativeColor(Color.GRAY)
                        .neutralText(R.string.dialog_msg_quit)
                        .neutralColor(Color.RED)
                        .title(R.string.dialog_msg_quit_edit)
                        .content(R.string.dialog_msg_has_no_saved)
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                FullEditActivity.this.finish();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                editComplete();
                            }
                        })
                        .show();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        mdPreviewView.loadMarkdown(contentEt.getText().toString(),
                MarkdownView.DEFAULT_MARKDOWN_CSS_FILE, MarkdownView.BASE_MARKDOWN_JS_FILES, true);
        mSwipeWindowHelper.setCanSlideBack(false);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        mSwipeWindowHelper.setCanSlideBack(true);
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        noteColor = selectedColor;
        ComponentHolder.getAppComponent().getEventBus().post(new NoteColorChangeEvent(selectedColor));
    }

    public int getNoteColor() {
        return noteColor;
    }
}
