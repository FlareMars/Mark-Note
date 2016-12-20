package com.flaremars.markandnote.common;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.flaremars.markandnote.util.SwipeWindowHelper;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    protected SwipeWindowHelper mSwipeWindowHelper;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(!supportSlideBack()) {
            return super.dispatchTouchEvent(ev);
        }

        if(mSwipeWindowHelper == null) {
            mSwipeWindowHelper = new SwipeWindowHelper(getWindow());
        }
        return mSwipeWindowHelper.processTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    /**
     * 是否支持滑动返回
     */
    protected boolean supportSlideBack() {
        return true;
    }
}
