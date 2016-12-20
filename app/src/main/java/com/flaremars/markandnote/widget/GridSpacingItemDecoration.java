package com.flaremars.markandnote.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by FlareMars on 2016/5/5.
 * 实现GridRecyclerView 的行列间距
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;
    private boolean hasHeaderView;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
        this.hasHeaderView = false;
    }
    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge, boolean hasHeaderView) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
        this.hasHeaderView = hasHeaderView;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;

        if (includeEdge) {
            int originLeft = outRect.left;
            int originRight = outRect.right;
            outRect.left = spacing - column * spacing / spanCount;
            outRect.right = (column + 1) * spacing / spanCount;

            if (!hasHeaderView) {
                if (position < spanCount) {
                    outRect.top = spacing;
                }
            } else {
                if (position != 0 && position < spanCount) {
                    outRect.top = spacing;
                }

                if (position == 0) {
                    outRect.left = originLeft;
                    outRect.right = originRight;
                }
            }
            outRect.bottom = spacing;
        } else {
            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;
            if (hasHeaderView) {
                if (position != 1 && position >= spanCount) {
                    outRect.top = spacing;
                }
            } else {
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GridSpacingItemDecoration that = (GridSpacingItemDecoration) o;

        if (spanCount != that.spanCount) return false;
        if (spacing != that.spacing) return false;
        if (includeEdge != that.includeEdge) return false;
        return hasHeaderView == that.hasHeaderView;

    }

    @Override
    public int hashCode() {
        int result = spanCount;
        result = 31 * result + spacing;
        result = 31 * result + (includeEdge ? 1 : 0);
        result = 31 * result + (hasHeaderView ? 1 : 0);
        return result;
    }
}
