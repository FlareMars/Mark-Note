package com.flaremars.markandnote.bean.editaction;

import android.os.Parcel;
import android.os.Parcelable;

import com.flaremars.markandnote.R;
import com.flaremars.markandnote.common.Constants;
import com.flaremars.markandnote.view.FullEditActivity;

/**
 * Created by FlareMars on 2016/11/14.
 */
public class CompleteEditAction extends EditActionBean {

    public CompleteEditAction() {
        super(R.mipmap.ic_action_mark);
    }

    public CompleteEditAction(Parcel in) {
        super(in);
    }

    @Override
    public void onAction(FullEditActivity context) {
        context.editComplete();
    }

    @Override
    public int describeContents() {
        return Constants.EDIT_ACTION_COMPLETE;
    }

    public static final Parcelable.Creator<CompleteEditAction> CREATOR = new Parcelable.Creator<CompleteEditAction>() {
        public CompleteEditAction createFromParcel(Parcel in) {
            return new CompleteEditAction(in);
        }

        public CompleteEditAction[] newArray(int size) {
            return new CompleteEditAction[size];
        }
    };
}
