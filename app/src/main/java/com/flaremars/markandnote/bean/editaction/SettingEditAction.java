package com.flaremars.markandnote.bean.editaction;

import android.os.Parcel;

import com.flaremars.markandnote.R;
import com.flaremars.markandnote.common.Constants;
import com.flaremars.markandnote.util.LogUtils;
import com.flaremars.markandnote.view.FullEditActivity;

/**
 * Created by FlareMars on 2016/11/14.
 */
public class SettingEditAction extends EditActionBean {

    public SettingEditAction() {
        super(R.mipmap.ic_action_setting);
    }

    public SettingEditAction(Parcel in) {
        super(in);
    }

    @Override
    public void onAction(FullEditActivity context) {
        LogUtils.logD("EditAction", "SettingEditAction");
    }

    @Override
    public int describeContents() {
        return Constants.EDIT_ACTION_SETTING;
    }

    public static final Creator<SettingEditAction> CREATOR = new Creator<SettingEditAction>() {
        public SettingEditAction createFromParcel(Parcel in) {
            return new SettingEditAction(in);
        }

        public SettingEditAction[] newArray(int size) {
            return new SettingEditAction[size];
        }
    };
}
