package com.flaremars.markandnote.view;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flaremars.markandnote.R;
import com.flaremars.markandnote.adapter.NoteAdapter;
import com.flaremars.markandnote.common.App;
import com.flaremars.markandnote.common.ComponentHolder;
import com.flaremars.markandnote.entity.Note;
import com.flaremars.markandnote.entity.RemoteNote;
import com.flaremars.markandnote.entity.User;
import com.flaremars.markandnote.event.DeleteNoteEvent;
import com.flaremars.markandnote.event.EditNoteEvent;
import com.flaremars.markandnote.event.PutTopNoteEvent;
import com.flaremars.markandnote.event.UpdateNoteListEvent;
import com.flaremars.markandnote.storage.NoteStorage;
import com.flaremars.markandnote.storage.UserStorage;
import com.flaremars.markandnote.util.Check;
import com.flaremars.markandnote.util.StringUtils;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;

public class NotesMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "NotesMainActivity";

    private View drawerHeaderView;
    private ImageView avatarIv;
    private TextView usernameTv;
    private TextView noteCountTv;

    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private RecyclerView contentRecyclerView;
    private List<Note> notes;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BmobUpdateAgent.update(this);

        setContentView(R.layout.activity_notes_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerHeaderView = navigationView.getHeaderView(0);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        contentRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        avatarIv = (ImageView) drawerHeaderView.findViewById(R.id.iv_avatar);
        usernameTv = (TextView) drawerHeaderView.findViewById(R.id.tv_username);
        noteCountTv = (TextView) drawerHeaderView.findViewById(R.id.tv_note_count);

        initData();
        initViews();
    }

    private void initData() {
        notes = new ArrayList<>();
        noteAdapter = new NoteAdapter(notes);
    }

    private void initViews() {
        fab.setIconDrawable(getPlusDrawable());
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        contentRecyclerView.setAdapter(noteAdapter);

        drawerHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserStorage.getInstance().getUserData() == null) {
                    Intent intent = new Intent(NotesMainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    UserStorage.getInstance().logout();
                    Intent intent = new Intent(NotesMainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    NotesMainActivity.this.finish();
                }
            }
        });
    }

    private void updateList() {
        notes.clear();
        notes.addAll(NoteStorage.getInstance().findAll());
        noteAdapter.notifyDataSetChanged();
    }

    private void updateList(int deletePosition) {
        notes.remove(deletePosition);
        noteAdapter.notifyItemRemoved(deletePosition);
    }

    private void updateUserData() {
        noteCountTv.setText(notes.size() + " 条笔记");
        User user = UserStorage.getInstance().getUserData();
        if (user != null) {
            usernameTv.setText(user.getUsername());
            if (user.getAvatarUrl() == null || StringUtils.INSTANCE.isEmpty(user.getAvatarUrl())) {
                avatarIv.setImageResource(R.mipmap.ic_launcher);
            } else {
                Picasso.with(this).load(user.getAvatarUrl()).into(avatarIv);
            }
        } else {
            usernameTv.setText("未登录账号");
            avatarIv.setImageResource(R.mipmap.ic_launcher);
        }
    }

    public void onNewNote(View button) {
        Intent intent = new Intent(this, FullEditActivity.class);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoteItemClick(Note note) {
        Intent intent = new Intent(this, ViewNoteActivity.class);
        intent.putExtra(ViewNoteActivity.PARAM_NOTE_ID, note.getId());
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoteDelete(DeleteNoteEvent event) {
        Note temp;
        int result = -1;
        for (int i = 0, size = notes.size();i < size;i++) {
            temp = notes.get(i);
            if (temp.getId().equals(event.getLocalId())) {
                result = i;
                break;
            }
        }
        if (result != -1) {
            updateList(result);
        } else {
            updateList();
        }

        // 禁用远端指定数据
        temp = NoteStorage.getInstance().findById(event.getLocalId());
        if (!Check.isNull(temp) && !StringUtils.INSTANCE.isEmpty(temp.getNoteId())) {
            RemoteNote remoteNote = new RemoteNote();
            remoteNote.setDeprecated(true);
            remoteNote.update(temp.getNoteId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.d(TAG, "delete successful!");
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }

        NoteStorage.getInstance().delete(event.getLocalId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoteEdit(EditNoteEvent event) {
        Intent intent = new Intent(this, FullEditActivity.class);
        intent.putExtra(FullEditActivity.PARAM_TARGET_NOTE_ID, event.getLocalId());
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotePutTop(PutTopNoteEvent event) {
        Note target = NoteStorage.getInstance().findById(event.getLocalId());
        if (target != null) {
            target.setPutTopFlag(event.getTop());
            NoteStorage.getInstance().update(target);

            updateList();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateNoteList(UpdateNoteListEvent event) {
        Toast.makeText(App.getContext(), "远端数据同步完成", Toast.LENGTH_SHORT).show();

        updateList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ComponentHolder.getAppComponent().getEventBus().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
        updateUserData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ComponentHolder.getAppComponent().getEventBus().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_export:
                break;
            case R.id.nav_import:
                break;
            case R.id.nav_statistics:
                break;
            case R.id.nav_md_syntax:
                break;
            case R.id.nav_setting:
                break;
            case R.id.nav_info:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_feedback:
                break;
            default:
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Drawable getPlusDrawable() {
        final float iconSize = getDimension(com.getbase.floatingactionbutton.R.dimen.fab_icon_size);
        final float iconHalfSize = iconSize / 2f;

        final float plusSize = getDimension(com.getbase.floatingactionbutton.R.dimen.fab_plus_icon_size);
        final float plusHalfStroke = getDimension(com.getbase.floatingactionbutton.R.dimen.fab_plus_icon_stroke) / 2f;
        final float plusOffset = (iconSize - plusSize) / 2f;

        final Shape shape = new Shape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                canvas.drawRect(plusOffset, iconHalfSize - plusHalfStroke, iconSize - plusOffset, iconHalfSize + plusHalfStroke, paint);
                canvas.drawRect(iconHalfSize - plusHalfStroke, plusOffset, iconHalfSize + plusHalfStroke, iconSize - plusOffset, paint);
            }
        };

        ShapeDrawable drawable = new ShapeDrawable(shape);

        final Paint paint = drawable.getPaint();
        paint.setColor(getResources().getColor(R.color.half_black));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        return drawable;
    }

    private float getDimension(@DimenRes int id) {
        return getResources().getDimension(id);
    }

}
