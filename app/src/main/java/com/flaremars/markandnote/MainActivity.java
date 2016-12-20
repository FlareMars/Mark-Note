package com.flaremars.markandnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.flaremars.markandnote.event.EventHolder;
import com.flaremars.markandnote.view.BookMarksFragment;
import com.flaremars.markandnote.view.FullEditActivity;
import com.flaremars.markandnote.view.NotesFragment;
import com.flaremars.markandnote.widget.NavigationBarView;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.update.BmobUpdateAgent;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private DrawerLayout drawer;
    private NavigationBarView bottomNavigationBarView;
    private ViewPager viewPager;
    private FloatingActionsMenu floatingActionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BmobUpdateAgent.initAppVersion();
        BmobUpdateAgent.update(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationBarView = (NavigationBarView) findViewById(R.id.main_navigation_bar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.floating_actions_menu);

        initFragments();
    }

    private void initFragments() {

        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), getFragments()));
        viewPager.addOnPageChangeListener(this);
        bottomNavigationBarView.setViewPager(viewPager);
    }

    private List<Fragment> getFragments() {

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new NotesFragment());
        fragments.add(new BookMarksFragment());
        return fragments;
    }

    private void collapseActionsMenu() {

        floatingActionsMenu.collapse();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        // do nothing
    }

    @Override
    public void onPageSelected(int position) {

        // do nothing
    }

    @Override
    public void onPageScrollStateChanged(int state) {

        if (state != ViewPager.SCROLL_STATE_IDLE) {
            collapseActionsMenu();
        }
    }

    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments;

        public MyFragmentPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
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

    public void onNewNote(View button) {
        Intent intent = new Intent(this, FullEditActivity.class);
        startActivity(intent);
        collapseActionsMenu();
    }

    public void onNewNoteWithTemplate(View button) {
        collapseActionsMenu();
    }

    public void onNewBookMark(View button) {
        collapseActionsMenu();
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Subscribe
    public void justForHolder(EventHolder holder) {

    }
}
