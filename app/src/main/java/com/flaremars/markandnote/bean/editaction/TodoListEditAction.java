package com.flaremars.markandnote.bean.editaction;

import android.os.Parcel;

import com.flaremars.markandnote.R;
import com.flaremars.markandnote.common.Constants;
import com.flaremars.markandnote.view.FullEditActivity;

/**
 * Created by FlareMars on 2016/11/14.
 */
public class TodoListEditAction extends EditActionBean {

    public TodoListEditAction() {
        super(R.mipmap.ic_action_list);
    }

    public TodoListEditAction(Parcel in) {
        super(in);
    }

    @Override
    public void onAction(FullEditActivity context) {
        context.insertTodoList();
    }

    @Override
    public int describeContents() {
        return Constants.EDIT_ACTION_TODOLIST;
    }

    public static final Creator<TodoListEditAction> CREATOR = new Creator<TodoListEditAction>() {
        public TodoListEditAction createFromParcel(Parcel in) {
            return new TodoListEditAction(in);
        }

        public TodoListEditAction[] newArray(int size) {
            return new TodoListEditAction[size];
        }
    };
}
