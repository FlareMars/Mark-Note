package com.flaremars.markandnote.entity;

import android.support.annotation.NonNull;

import com.flaremars.annotation.Column;
import com.flaremars.annotation.Entity;
import com.flaremars.markandnote.util.StringUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FlareMars on 2016/12/3.
 */
@Entity(name = "Note", objectClass = Note.class)
public class Note extends DataSupport implements Comparable<Note> {

    @Column(required = false, updated = false, findBy = true)
    private Integer id;
    @Column(updated = true, findBy = true)
    private String noteId;
    @Column(updated = false)
    private Long date;
    @Column
    private String title;
    @Column
    private String content;
    @Column(required = false)
    private String pictureUrls; // ',' split
    @Column(updated = true)
    private Boolean putTopFlag;
    @Column(required = false)
    private Long synchronousTime; // 远端同步时间
    @Column(required = false)
    private Long modifiedTime; // 本地最近修改时间

    public Note(Integer id, String noteId, Long date, String title, String content, String pictureUrls) {
        this.id = id;
        this.noteId = noteId;
        this.date = date;
        this.title = title;
        this.content = content;
        this.pictureUrls = pictureUrls;
        this.putTopFlag = false;
    }

    public Note() {
        this.putTopFlag = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPictureUrls() {
        return pictureUrls;
    }

    public void setPictureUrls(String pictureUrls) {
        this.pictureUrls = pictureUrls;
    }

    public void setPictureUrls(List<String> pictureUrlsList) {
        StringBuilder sb = new StringBuilder();

        for (String url : pictureUrlsList) {
            sb.append(url);
            sb.append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        this.pictureUrls = sb.toString();
    }

    public List<String> getPictureUrlsList() {
        final List<String> result = new ArrayList<>();
        if (pictureUrls != null && !StringUtils.INSTANCE.isEmpty(pictureUrls)) {
            for (String url : pictureUrls.split(",")) {
                if (!StringUtils.INSTANCE.isEmpty(url)) {
                    result.add(url);
                }
            }
        }
        return result;
    }

    public Boolean getPutTopFlag() {
        return putTopFlag;
    }

    public void setPutTopFlag(Boolean putTopFlag) {
        this.putTopFlag = putTopFlag;
    }

    public Long getModifiedTime() {
        if (modifiedTime == null) {
            modifiedTime = 0L;
        }
        return modifiedTime;
    }

    public void setModifiedTime(Long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Long getSynchronousTime() {
        if (synchronousTime == null || synchronousTime == 0L) {
            synchronousTime = -1L;
        }
        return synchronousTime;
    }

    public void setSynchronousTime(Long synchronousTime) {
        this.synchronousTime = synchronousTime;
    }

    public boolean needSynchronized() {
        return getSynchronousTime() < getModifiedTime();
    }

    @Override
    public int compareTo(@NonNull Note another) {
        if (putTopFlag.compareTo(another.putTopFlag) == 0) {
            return date.compareTo(another.date);
        } else {
            return putTopFlag.compareTo(another.putTopFlag);
        }
    }
}
