package com.flaremars.markandnote.bean;

/**
 * Created by FlareMars on 2016/5/5.
 * 拥有可被选择属性的 图片bean
 */
public class SelectedPictureItem {

    private String path;
    private String originalPath;
    private boolean isSelected;

    public SelectedPictureItem() {
        isSelected = false;
    }

    public SelectedPictureItem(String path, String originalPath) {
        this();
        this.path = path;
        this.originalPath = originalPath;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
