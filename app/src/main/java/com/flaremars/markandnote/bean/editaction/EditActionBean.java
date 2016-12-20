package com.flaremars.markandnote.bean.editaction;

import android.os.Parcel;
import android.os.Parcelable;

import com.flaremars.markandnote.R;
import com.flaremars.markandnote.view.FullEditActivity;

/**
 * Created by FlareMars on 2016/11/14.
 */
public abstract class EditActionBean implements Parcelable {

    private int iconResId;

    public abstract void onAction(FullEditActivity context);

    public EditActionBean(int iconResId) {
        this.iconResId = iconResId;
    }

    public EditActionBean() {
        this.iconResId = R.mipmap.ic_launcher;
    }

    public EditActionBean(Parcel in) {
        readFromParcel(in);
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    private void readFromParcel(Parcel in) {
        iconResId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(iconResId);
    }

}
