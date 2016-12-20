package com.flaremars.markandnote.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.flaremars.markandnote.R;

import java.util.ArrayList;
import java.util.List;

public class NavigationBarView extends LinearLayout implements OnClickListener {

    private List<BottomViewBean> bottomSingleViewList;
    private int currentPosition = 0;
    private boolean isClick = false;
    private ViewPager viewPager;

    public NavigationBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    public NavigationBarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public NavigationBarView(Context context) {
        super(context);

        init(context);
    }

    private void init(Context context){

        setOrientation(LinearLayout.HORIZONTAL);
        setBackgroundResource(R.drawable.bg_tags_bar);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.navigation_bar, this);
    }

    private void initChildren() {

        bottomSingleViewList = new ArrayList<>();
        for (int i = 0, size = getChildCount();i < size;i++) {
            View child = getChildAt(i);
            if (child instanceof BottomSingleView) {
                bottomSingleViewList.add(new BottomViewBean((BottomSingleView)child, child.getId()));
                child.setOnClickListener(this);

                if (i == 0) {
                    bottomSingleViewList.get(i).view.setProgress(1f);
                } else {
                    bottomSingleViewList.get(i).view.setProgress(0f);
                }
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initChildren();
    }

    public void setViewPager(ViewPager viewPager) {

        this.viewPager = viewPager ;
        this.viewPager.addOnPageChangeListener(listener);
    }

    private  void setChildProgress(int position , float progress){
        if (position >= 0 && position < bottomSingleViewList.size()) {
            bottomSingleViewList.get(position).view.setProgress(progress);
        }
    }

    private void restoreState(int lastPosition) {
        if (lastPosition >= 0 && lastPosition < bottomSingleViewList.size()) {
            bottomSingleViewList.get(lastPosition).view.setProgress(0f);
        }
    }

    private OnPageChangeListener listener = new OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int arg2) {
            if (!isClick) {
                setChildProgress(position, 1 - positionOffset);
                setChildProgress(position + 1, positionOffset);
            }
        }

        @Override
        public void onPageSelected(int position) {
            currentPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == 0) {
                isClick = false;
            }
        }
    };

    @Override
    public void onClick(View v) {
        isClick = true;
        int id = v.getId();
        int targetPosition = 0;
        for (int i = 0;i < bottomSingleViewList.size();i++) {
            if (id == bottomSingleViewList.get(i).id) {
                targetPosition = i;
            }
        }

        if (targetPosition == currentPosition) {
            return;
        }

        restoreState(currentPosition);
        bottomSingleViewList.get(targetPosition).view.setProgress(1f);
        viewPager.setCurrentItem(targetPosition);
        currentPosition = targetPosition;
    }

    private class BottomViewBean {
        private BottomSingleView view;
        private int id;

        public BottomViewBean(BottomSingleView view, int id) {
            this.view = view;
            this.id = id;
        }
    }
}
