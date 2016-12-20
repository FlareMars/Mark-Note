package com.flaremars.markandnote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flaremars.markandnote.R;
import com.flaremars.markandnote.common.ComponentHolder;
import com.flaremars.markandnote.entity.Note;
import com.flaremars.markandnote.event.DeleteNoteEvent;
import com.flaremars.markandnote.event.EditNoteEvent;
import com.flaremars.markandnote.event.PutTopNoteEvent;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

/**
 * Created by FlareMars on 2016/11/17.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<Note> entities;

    public NoteAdapter(List<Note> notes) {
        this.entities = notes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Note note = entities.get(position);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(note.getDate());
        holder.dayTv.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");
        holder.monthTv.setText((calendar.get(Calendar.MONTH)+1) + "月");

        holder.titleTv.setText(note.getTitle());
        holder.contentTv.setText(note.getContent().trim());

        List<String> images = note.getPictureUrlsList();
        final Context context = holder.itemView.getContext();

        holder.firstImageIv.setImageResource(R.drawable.bg_none);
        holder.firstImageIv.setVisibility(View.GONE);
        holder.secondImageIv.setImageResource(R.drawable.bg_none);
        holder.secondImageIv.setVisibility(View.GONE);
        if (images.size() >= 1) {
            holder.firstImageIv.setVisibility(View.VISIBLE);
            loadImage(context, holder.firstImageIv, images.get(0));
        }
        if (images.size() >= 2) {
            holder.secondImageIv.setVisibility(View.VISIBLE);
            loadImage(context, holder.secondImageIv, images.get(1));
        }

        if (note.getPutTopFlag()) {
            holder.itemView.setBackgroundResource(R.drawable.bg_selectable_item_special);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_selectable_item);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentHolder.getAppComponent().getEventBus().post(note);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MaterialDialog.Builder(v.getContext())
                        .items(note.getPutTopFlag() ? R.array.note_actions_with_top : R.array.note_actions)
                        .itemsGravity(GravityEnum.CENTER)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:    // 置顶 or 取消置顶
                                        ComponentHolder.getAppComponent().getEventBus().post(new PutTopNoteEvent(note.getId(), note.getNoteId(), !note.getPutTopFlag()));
                                        break;
                                    case 1:    // 查看
                                        ComponentHolder.getAppComponent().getEventBus().post(note);
                                        break;
                                    case 2:    // 编辑
                                        ComponentHolder.getAppComponent().getEventBus().post(new EditNoteEvent(note.getId(), note.getNoteId()));
                                        break;
                                    case 3:    // 删除
                                        ComponentHolder.getAppComponent().getEventBus().post(new DeleteNoteEvent(note.getId(), note.getNoteId()));
                                        break;
                                    default:
                                }
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    private void loadImage(Context context, ImageView imageView, String url) {
        if (!url.startsWith("http") && !url.startsWith("file")) {
            url = "file://" + url;
        }
        Picasso.with(context).load(url)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_picture_broken)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView dayTv;
        private TextView monthTv;
        private TextView titleTv;
        private TextView contentTv;
        private ImageView firstImageIv;
        private ImageView secondImageIv;

        public ViewHolder(View itemView) {
            super(itemView);
            dayTv = (TextView) itemView.findViewById(R.id.tv_day);
            monthTv = (TextView) itemView.findViewById(R.id.tv_month);
            titleTv = (TextView) itemView.findViewById(R.id.tv_title);
            contentTv = (TextView) itemView.findViewById(R.id.tv_content);
            firstImageIv = (ImageView) itemView.findViewById(R.id.iv_first_image);
            secondImageIv = (ImageView) itemView.findViewById(R.id.iv_second_image);
        }
    }
}
