package com.flaremars.markandnote.widget;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.flaremars.markandnote.bean.editaction.ColorEditAction;
import com.flaremars.markandnote.bean.editaction.CompleteEditAction;
import com.flaremars.markandnote.bean.editaction.LinkEditAction;
import com.flaremars.markandnote.bean.editaction.PictureEditAction;
import com.flaremars.markandnote.bean.editaction.PreviewEditAction;
import com.flaremars.markandnote.bean.editaction.RedoEditAction;
import com.flaremars.markandnote.bean.editaction.SettingEditAction;
import com.flaremars.markandnote.bean.editaction.TodoListEditAction;
import com.flaremars.markandnote.bean.editaction.UndoEditAction;
import com.flaremars.markandnote.common.BaseActivity;
import com.flaremars.markandnote.view.ActionsFragment;
import com.flaremars.markandnote.view.FullEditActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FlareMars on 2016/11/14.
 * todo 考虑加上滑动显示静止时消失的位置指示器
 */
public class EditActionsPanel extends ViewPager {
    public EditActionsPanel(Context context) {
        super(context);
        init();
    }

    public EditActionsPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setAdapter(new StatusViewPagerAdapter(((BaseActivity)getContext()).getSupportFragmentManager(), initFragments()));
    }

    private List<Fragment> initFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(ActionsFragment.newInstance(
                new CompleteEditAction(),
                new PreviewEditAction(),
                new UndoEditAction(),
                new RedoEditAction(),
                new ColorEditAction(((FullEditActivity)getContext()).getNoteColor())));

        fragments.add(ActionsFragment.newInstance(
                new PictureEditAction(),
                new LinkEditAction(),
                new TodoListEditAction(),
                new SettingEditAction()));

        return fragments;
    }

    private class StatusViewPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments;

        public StatusViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
