package com.flaremars.markandnote.view;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flaremars.markandnote.R;
import com.flaremars.markandnote.bean.editaction.EditActionBean;
import com.flaremars.markandnote.util.LogUtils;
import com.flaremars.markandnote.widget.ActionIconView;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.List;

public class ActionsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ActionsFragment";
    private static final String KEY_ACTIONS_KEY = "actions";

    private LinearLayout actionsContainer;
    private SparseArray<EditActionBean> editActions;
    private List<ActionIconView> actionIconViews;

    public ActionsFragment() {

    }

    public static ActionsFragment newInstance(EditActionBean...actions) {
        ActionsFragment fragment = new ActionsFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(KEY_ACTIONS_KEY, actions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editActions = new SparseArray<>();
        Bundle args = getArguments();
        if (args != null) {
            Parcelable[] actions = args.getParcelableArray(KEY_ACTIONS_KEY);
            if (actions != null && actions.length > 0) {
                for (Parcelable action : actions) {
                    editActions.put(action.describeContents(), (EditActionBean) action);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FloatingActionsMenu menu;
        return inflater.inflate(R.layout.fragment_actions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        actionsContainer = (LinearLayout) view.findViewById(R.id.container);
        init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (ActionIconView view : actionIconViews) {
            view.unRegisterEventBus();
        }
        actionIconViews.clear();
    }

    private void init() {
        actionIconViews = new ArrayList<>();
        for (int i = 0, size = editActions.size();i < size;i++) {
            EditActionBean actionBean = editActions.valueAt(i);
            actionsContainer.addView(createActionView(actionBean));
        }
    }

    private View createActionView(EditActionBean action) {
        ActionIconView actionView = new ActionIconView(getContext());
        actionView.setScaleType(ImageView.ScaleType.CENTER);
        actionView.setBackgroundResource(R.drawable.edit_action_bg);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1f;
        actionView.setLayoutParams(layoutParams);

        actionView.setId(action.describeContents());
        actionView.setDescribeContents(action.describeContents());
        actionView.setImageResource(action.getIconResId());
        actionView.setOnClickListener(this);

        actionIconViews.add(actionView);
        return actionView;
    }

    @Override
    public void onClick(View v) {
        EditActionBean action = editActions.get(v.getId());
        if (action != null) {
            action.onAction((FullEditActivity)getContext());
        } else {
            LogUtils.logE(TAG, "view id = " + v.getId() + " not found in actions!");
        }
    }
}
