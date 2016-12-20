package com.flaremars.markandnote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flaremars.markandnote.R;
import com.flaremars.markandnote.bean.SelectedPictureItem;
import com.flaremars.markandnote.util.DisplayUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by FlareMars on 2016/5/5.
 */
public class SelectedPictureAdapter extends RecyclerView.Adapter<SelectedPictureAdapter.ViewHolder>{

    private static final String TAG = "SelectedPictureAdapter";

    private int requestWidth;
    private int requestHeight;

    private int sizeLimit;
    private int curSelectedSize = 0;
    private List<SelectedPictureItem> pictureItems;

    private Context context;

    public SelectedPictureAdapter(Context context, List<SelectedPictureItem> pictureItems, int sizeLimit) {
        this.pictureItems = pictureItems;
        this.context = context;
        this.sizeLimit = sizeLimit;
        DisplayUtils.DisplayInfo displayInfo = DisplayUtils.INSTANCE.getSystemInfo(context);
        requestWidth = (displayInfo.getScreenWidth() - DisplayUtils.INSTANCE.dp2px(context,32.0f)) / 3;
        requestHeight = requestWidth;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_picture,parent,false);
        ImageView contentImage = (ImageView) itemView.findViewById(R.id.iv_content);
        ImageView tagImage = (ImageView) itemView.findViewById(R.id.iv_selected_tag);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(requestWidth,requestHeight);
        contentImage.setLayoutParams(layoutParams);
        tagImage.setLayoutParams(layoutParams);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SelectedPictureItem item = pictureItems.get(position);
        Picasso.with(context).load(new File(item.getPath())).resize(requestWidth,requestHeight).centerCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_picture_broken).into(holder.content);
        holder.selectedTag.setVisibility(item.isSelected() ? View.VISIBLE : View.INVISIBLE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!item.isSelected()) { //准备要被选择
                    if (curSelectedSize >= sizeLimit) {
                        Toast.makeText(holder.itemView.getContext(), "已超过限制数目", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                item.setSelected(!item.isSelected());
                if (item.isSelected()) {
                    curSelectedSize++;
                } else {
                    curSelectedSize--;
                }
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pictureItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView selectedTag;
        private ImageView content;

        public ViewHolder(View itemView) {
            super(itemView);
            selectedTag = (ImageView) itemView.findViewById(R.id.iv_selected_tag);
            content = (ImageView) itemView.findViewById(R.id.iv_content);
        }
    }
}
