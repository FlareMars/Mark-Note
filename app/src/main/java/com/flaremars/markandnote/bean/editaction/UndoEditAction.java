package com.flaremars.markandnote.bean.editaction;

import android.os.Parcel;

import com.flaremars.markandnote.R;
import com.flaremars.markandnote.common.Constants;
import com.flaremars.markandnote.view.FullEditActivity;

/**
 * Created by FlareMars on 2016/11/14.
 */
public class UndoEditAction extends EditActionBean {

    public UndoEditAction() {
        super(R.mipmap.ic_action_undo);
    }

    public UndoEditAction(Parcel in) {
        super(in);
    }

    @Override
    public void onAction(FullEditActivity context) {
        context.editUndo();
    }

    @Override
    public int describeContents() {
        return Constants.EDIT_ACTION_UNDO;
    }

    public static final Creator<UndoEditAction> CREATOR = new Creator<UndoEditAction>() {
        public UndoEditAction createFromParcel(Parcel in) {
            return new UndoEditAction(in);
        }

        public UndoEditAction[] newArray(int size) {
            return new UndoEditAction[size];
        }
    };
}
