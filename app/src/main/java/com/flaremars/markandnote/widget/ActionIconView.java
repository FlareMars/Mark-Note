package com.flaremars.markandnote.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.flaremars.markandnote.common.ComponentHolder;
import com.flaremars.markandnote.common.Constants;
import com.flaremars.markandnote.event.NoteColorChangeEvent;
import com.flaremars.markandnote.util.DisplayUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by FlareMars on 2016/11/14.
 * 用于编辑操作按钮
 */
public class ActionIconView extends ImageView {

    private int iconResId;
    private int describeContents;

    public ActionIconView(Context context) {
        super(context);
    }

    public ActionIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageResource(int resId) {
        this.iconResId = resId;

        if (isColorAction()) {
            ComponentHolder.getAppComponent().getEventBus().register(this);
            setImageDrawable(createCircleDrawable(resId));
        } else {
            super.setImageResource(resId);
        }
    }

    public void setDescribeContents(int describeContents) {
        this.describeContents = describeContents;
    }

    public void unRegisterEventBus() {
        if (isColorAction()) {
            ComponentHolder.getAppComponent().getEventBus().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onColorChanged(NoteColorChangeEvent event) {
        this.iconResId = -event.getColor();
        setImageDrawable(createCircleDrawable(event.getColor()));
    }

    private Drawable createCircleDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setSize(DisplayUtils.INSTANCE.dp2px(getContext(), 16f), DisplayUtils.INSTANCE.dp2px(getContext(), 16f));

        return drawable;
    }

    private boolean isColorAction() {
        return describeContents == Constants.EDIT_ACTION_COLOR;
    }
}
