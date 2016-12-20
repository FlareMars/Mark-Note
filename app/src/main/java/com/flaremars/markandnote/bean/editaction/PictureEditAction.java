package com.flaremars.markandnote.bean.editaction;

import android.content.Intent;
import android.os.Parcel;

import com.flaremars.markandnote.R;
import com.flaremars.markandnote.common.Constants;
import com.flaremars.markandnote.view.FullEditActivity;
import com.flaremars.markandnote.view.SelectPicturesActivity;

/**
 * Created by FlareMars on 2016/11/14.
 */
public class PictureEditAction extends EditActionBean {

    public PictureEditAction() {
        super(R.mipmap.ic_action_picture);
    }

    public PictureEditAction(Parcel in) {
        super(in);
    }

    @Override
    public void onAction(FullEditActivity context) {
        Intent intent = new Intent(context, SelectPicturesActivity.class);
        context.startActivityForResult(intent, FullEditActivity.REQUEST_CODE_SELECT_PICTURE);
    }

    @Override
    public int describeContents() {
        return Constants.EDIT_ACTION_PICTURE;
    }

    public static final Creator<PictureEditAction> CREATOR = new Creator<PictureEditAction>() {
        public PictureEditAction createFromParcel(Parcel in) {
            return new PictureEditAction(in);
        }

        public PictureEditAction[] newArray(int size) {
            return new PictureEditAction[size];
        }
    };
}
