package com.flaremars.markandnote.bean.editaction;

import android.os.Parcel;

import com.flaremars.markandnote.R;
import com.flaremars.markandnote.common.Constants;
import com.flaremars.markandnote.view.FullEditActivity;

/**
 * Created by FlareMars on 2016/11/14.
 */
public class RedoEditAction extends EditActionBean {

    public RedoEditAction() {
        super(R.mipmap.ic_action_redo);
    }

    public RedoEditAction(Parcel in) {
        super(in);
    }

    @Override
    public void onAction(FullEditActivity context) {
        context.editRedo();
    }

    @Override
    public int describeContents() {
        return Constants.EDIT_ACTION_REDO;
    }

    public static final Creator<RedoEditAction> CREATOR = new Creator<RedoEditAction>() {
        public RedoEditAction createFromParcel(Parcel in) {
            return new RedoEditAction(in);
        }

        public RedoEditAction[] newArray(int size) {
            return new RedoEditAction[size];
        }
    };
}
