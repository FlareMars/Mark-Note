package com.flaremars.markandnote.bean.editaction;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flaremars.markandnote.R;
import com.flaremars.markandnote.common.Constants;
import com.flaremars.markandnote.view.FullEditActivity;

/**
 * Created by FlareMars on 2016/11/14.
 */
public class LinkEditAction extends EditActionBean {

    public LinkEditAction() {
        super(R.mipmap.ic_action_link);
    }

    public LinkEditAction(Parcel in) {
        super(in);
    }

    @Override
    public void onAction(final FullEditActivity context) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.edit_action_insert_link)
                .customView(R.layout.dialog_edit_link, true)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditText titleEt = (EditText) dialog.findViewById(R.id.et_title);
                        EditText urlEt = (EditText) dialog.findViewById(R.id.et_url);

                        context.insertLink(titleEt.getText().toString(), urlEt.getText().toString());
                    }
                }).build();
        dialog.show();
    }

    @Override
    public int describeContents() {
        return Constants.EDIT_ACTION_LINK;
    }

    public static final Creator<LinkEditAction> CREATOR = new Creator<LinkEditAction>() {
        public LinkEditAction createFromParcel(Parcel in) {
            return new LinkEditAction(in);
        }

        public LinkEditAction[] newArray(int size) {
            return new LinkEditAction[size];
        }
    };
}
