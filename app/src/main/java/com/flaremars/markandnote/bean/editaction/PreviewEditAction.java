package com.flaremars.markandnote.bean.editaction;

import android.os.Parcel;

import com.flaremars.markandnote.R;
import com.flaremars.markandnote.common.Constants;
import com.flaremars.markandnote.view.FullEditActivity;

/**
 * Created by FlareMars on 2016/11/14.
 */
public class PreviewEditAction extends EditActionBean {

    public PreviewEditAction() {
        super(R.mipmap.ic_action_preview);
    }

    public PreviewEditAction(Parcel in) {
        super(in);
    }

    @Override
    public void onAction(FullEditActivity context) {
        context.togglePreview();
    }

    @Override
    public int describeContents() {
        return Constants.EDIT_ACTION_PREVIEW;
    }

    public static final Creator<PreviewEditAction> CREATOR = new Creator<PreviewEditAction>() {
        public PreviewEditAction createFromParcel(Parcel in) {
            return new PreviewEditAction(in);
        }

        public PreviewEditAction[] newArray(int size) {
            return new PreviewEditAction[size];
        }
    };
}
