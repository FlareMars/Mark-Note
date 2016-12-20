package com.flaremars.markandnote.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.flaremars.markandnote.common.ComponentHolder;
import com.flaremars.markandnote.common.Constants;
import com.flaremars.markandnote.entity.Note;
import com.flaremars.markandnote.entity.RemoteNote;
import com.flaremars.markandnote.event.UpdateNoteListEvent;
import com.flaremars.markandnote.storage.NoteStorage;
import com.flaremars.markandnote.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by FlareMars on 2016/12/19
 */
public class SynchronousService extends IntentService {

    public static final String TAG = "SynchronousService";

    public static final String ACTION_INTENT = "ActionIntent";
    public static final int ACTION_PULL = 0; // 从远端同步数据
    public static final int ACTION_PUSH = 1; // 同步数据到远端

    public SynchronousService() {
        this(TAG);
    }

    public SynchronousService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int action = intent.getIntExtra(ACTION_INTENT, ACTION_PULL);
        String userId = ComponentHolder.getAppComponent().getSharedPrefHelper().getString(Constants.USERKEY_ID);
        if (userId == null || StringUtils.INSTANCE.isEmpty(userId)) {
            Log.e(TAG, "userId is empty!");
            return;
        }

        if (action == ACTION_PULL) {
            pullNotes(userId);
        } else if (action == ACTION_PUSH){
            try {
                pushNotes(userId);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * 同步数据到远端
     * 1. 获取所有的本地笔记数据
     * 2. 筛选出synchronousTime < modifiedTime的数据
     * 3. 遍历剩余笔记，切分内容
     *  4. 抽取本地图片路径（网络路径图片不作处理）
     *  5. 上传本地图片到Bmob（服务器），修改笔记内容图片路径为网络路径
     * 6. 同步数据
     * 7. 更新本地笔记的synchronousTime
     */
    private void pushNotes(String userId) throws IOException, JSONException {
        // 1. 获取所有的本地笔记数据
        List<Note> localNotes = NoteStorage.getInstance().findAll();

        // 2. 筛选出synchronousTime < modifiedTime的数据
        List<Note> needSynchronized = new ArrayList<>();
        for (Note temp : localNotes) {
            if (temp.needSynchronized()) {
                needSynchronized.add(temp);
            }
        }

        Pattern pattern = Pattern.compile("!\\[\\S+]\\(\\S+\\)");
        for (final Note temp : needSynchronized) { // 3. 遍历剩余笔记，切分内容
            final StringBuilder noteData = new StringBuilder(temp.getContent());
            final Matcher matcher = pattern.matcher(noteData);
            while (matcher.find()) {
                String content = matcher.group();
                final int urlStart = content.indexOf("(") + 1;
                int urlEnd = content.length() - 1;
                final String pictureUrl = content.substring(urlStart, urlEnd);
                Log.d(TAG, pictureUrl + " " + matcher.start() + " " + matcher.end());

                if (pictureUrl.startsWith("file")) { // 4. 抽取本地图片路径（网络路径图片不作处理）
                    boolean hasUploaded = ComponentHolder.getAppComponent().getSharedPrefHelper().getBool(pictureUrl);
                    if (!hasUploaded) {
                        Log.d(TAG, "file[" + pictureUrl + "] need to be uploaded!" );
                        File file = new File(pictureUrl.substring(7));
                        String responseUrl = bmobSynchronizedUpload(file);


                        if (StringUtils.INSTANCE.isEmpty(responseUrl)) {
                            Log.d(TAG, "file[" + pictureUrl + "] uploaded fail!");
                            continue;
                        }
                        Log.d(TAG, "file[" + pictureUrl + "] uploaded finished! url = " + responseUrl);
                        // 5. 上传本地图片到Bmob（服务器），修改笔记内容图片路径为网络路径
                        noteData.replace(matcher.start() + urlStart, matcher.end()-1, responseUrl);
                        ComponentHolder.getAppComponent().getSharedPrefHelper().saveBool(pictureUrl, true);
                    }
                }
            }

            // 6. 同步数据
            RemoteNote remoteNote = new RemoteNote();
            remoteNote.setDate(temp.getDate());
            remoteNote.setTitle(temp.getTitle());
            remoteNote.setContent(noteData.toString());
            remoteNote.setPutTopFlag(temp.getPutTopFlag());
            remoteNote.setUserId(userId);
            if (temp.getNoteId() != null && !StringUtils.INSTANCE.isEmpty(temp.getNoteId())) {
                remoteNote.setObjectId(temp.getNoteId());
                remoteNote.update(temp.getNoteId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Log.d(TAG, "note update successful");
                            temp.setSynchronousTime(new Date().getTime());
                            NoteStorage.getInstance().update(temp);
                        } else {
                            Log.e(TAG, e.getErrorCode() + " " + e.getMessage());
                        }
                    }
                });
            } else {
                remoteNote.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            Log.d(TAG, "note save successful");
                            temp.setNoteId(objectId);
                            temp.setSynchronousTime(new Date().getTime());
                            NoteStorage.getInstance().update(temp);
                        } else {
                            Log.e(TAG, e.getErrorCode() + " " + e.getMessage());
                        }
                    }
                });
            }
        }
    }

    private String bmobSynchronizedUpload(File file) throws IOException, JSONException {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        Request request = new Request.Builder()
                .url("https://api.bmob.cn/2/files/" + file.getName())
                .addHeader("X-Bmob-Application-Id", "719fc6c14fd5dea55bbc22697bd78814")
                .addHeader("X-Bmob-REST-API-Key", "e65543bf32d99fc1d82d320f7d0a2ba8")
                .addHeader("Content-Type", "application/octet-stream")
                .post(requestBody)
                .build();
        Response response = ComponentHolder.getAppComponent().getOKHttpClient().newCall(request).execute();
        JSONObject responseJson = new JSONObject(response.body().string());
        Log.d(TAG, responseJson.toString());
        return responseJson.has("url") ? responseJson.getString("url") : "";
    }

    /**
     * 从远端同步数据（只在重新登录的时候进行）
     * 1. 根据登录用户ID，拉取笔记内容
     * 2. 直接写入本地数据库，synchronousTime和modifiedTime设置为当前时间
     */
    private void pullNotes(String userId) {
        BmobQuery<RemoteNote> query = new BmobQuery<>();
        query.addWhereEqualTo("userId", userId);
        query.addWhereNotEqualTo("isDeprecated", true);
        query.findObjects(new FindListener<RemoteNote>() {
            @Override
            public void done(List<RemoteNote> list, BmobException e) {
                if (e == null) {
                    Log.d(TAG, "pull notes data successful! size = " + list.size());
                    for (RemoteNote note : list) {
                        Date current = new Date();

                        Note local = new Note();
                        local.setNoteId(note.getObjectId());
                        local.setContent(note.getContent());
                        local.setDate(note.getDate());
                        local.setTitle(note.getTitle());
                        local.setPutTopFlag(note.getPutTopFlag());
                        local.setModifiedTime(current.getTime());
                        local.setSynchronousTime(current.getTime());

                        Pattern pattern = Pattern.compile("!\\[\\S+]\\(\\S+\\)");
                        Matcher matcher = pattern.matcher(note.getContent());

                        List<String> urls = new ArrayList<>();
                        while (matcher.find()) {
                            String content = matcher.group();
                            urls.add(content.substring(content.indexOf("(") + 1, content.length()-1));
                        }
                        local.setPictureUrls(urls);

                        NoteStorage.getInstance().save(local);
                    }

                    ComponentHolder.getAppComponent().getEventBus().post(new UpdateNoteListEvent());
                } else {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }
}
