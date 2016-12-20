package com.flaremars.markandnote.bean.editaction;

import android.os.Parcel;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.flaremars.markandnote.R;
import com.flaremars.markandnote.common.Constants;
import com.flaremars.markandnote.view.FullEditActivity;

/**
 * Created by FlareMars on 2016/11/14.
 */
public class ColorEditAction extends EditActionBean {

    public ColorEditAction(int color) {
        super(color);
    }

    public ColorEditAction(Parcel in) {
        super(in);
    }

    @Override
    public void onAction(FullEditActivity context) {
        new ColorChooserDialog.Builder(context, R.string.action_color_selector_title)
                .doneButton(R.string.md_done_label)
                .cancelButton(R.string.md_cancel_label)
                .customColors(R.array.custom_colors, null)
                .preselect(context.getNoteColor())
                .allowUserColorInput(false)
                .show();
    }

    @Override
    public int describeContents() {
        return Constants.EDIT_ACTION_COLOR;
    }

    public static final Creator<ColorEditAction> CREATOR = new Creator<ColorEditAction>() {
        public ColorEditAction createFromParcel(Parcel in) {
            return new ColorEditAction(in);
        }

        public ColorEditAction[] newArray(int size) {
            return new ColorEditAction[size];
        }
    };
}
