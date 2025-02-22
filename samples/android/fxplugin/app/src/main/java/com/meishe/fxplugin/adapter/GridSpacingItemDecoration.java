package com.meishe.fxplugin.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.meishe.fxplugin.MSApplication;
import com.meishe.fxplugin.R;
import com.meishe.fxplugin.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import static com.meishe.fxplugin.view.MediaFragment.GRIDITEMCOUNT;


public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int screenWidth;
    private int itemWidth;
    private List<Integer> spanRightList;
    int marginSizeStart;
    int marginSizeMiddle;

    public GridSpacingItemDecoration(Context context, int spanCount) {
        this.spanCount = spanCount;
        int marginSizeLeftAndRight = (int) MSApplication.getmContext().getResources().getDimension(R.dimen.select_recycle_marginLeftAndRight);
        marginSizeStart = (int) MSApplication.getmContext().getResources().getDimension(R.dimen.select_item_start_end);
        marginSizeMiddle = (int) MSApplication.getmContext().getResources().getDimension(R.dimen.select_item_between);
        screenWidth = ScreenUtils.getScreenWidth(context) - marginSizeLeftAndRight * 2;
        spanRightList = new ArrayList<>();
        this.itemWidth = (screenWidth - marginSizeStart * 2 - marginSizeMiddle * (GRIDITEMCOUNT - 1)) / GRIDITEMCOUNT;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int surplus = screenWidth - spanCount * itemWidth;
        if (surplus <= 0) {
            return;
        }
        int startSpace = ScreenUtils.dip2px(MSApplication.getmContext(), 3);
        int column = position % spanCount; // item column

        int itemWidthInScreen = screenWidth / spanCount;
        int spaceBetweenItems = marginSizeMiddle;
        if (column == 0) {
            outRect.left = startSpace;
            outRect.right = itemWidthInScreen - itemWidth - outRect.left;
            spanRightList.add(outRect.right);
        } else if (column == (spanCount - 1)) {
            outRect.left = spaceBetweenItems - spanRightList.get(column - 1);
            outRect.right = itemWidthInScreen - itemWidth - outRect.left;
            spanRightList.add(outRect.right);
        } else {
            outRect.left = spaceBetweenItems - spanRightList.get(column - 1);
            outRect.right = itemWidthInScreen - itemWidth - outRect.left;
            spanRightList.add(outRect.right);
        }

    }
}