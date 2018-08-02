package com.cilenco.liveadapter.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.cilenco.liveadapter.utils.SimpleSwipeCallback.ItemSwipeCallback;
import com.cilenco.liveadapter.utils.SimpleSwipeCallback.SwipeDirections;

public class SimpleSwipeDragCallback extends SimpleDragCallback {
    public interface ItemSwipeDragCallback extends ItemSwipeCallback, ItemDragCallback {}

    private final SimpleSwipeCallback simpleSwipeCallback;

    public SimpleSwipeDragCallback(@NonNull ItemSwipeDragCallback callback) {
        this(SimpleSwipeCallback.ALL, UP_DOWN, callback);
    }

    public SimpleSwipeDragCallback(@SwipeDirections int swipeDirections, @DragDirections int dragDirections, @NonNull ItemSwipeDragCallback callback) {
        super(dragDirections, callback);
        simpleSwipeCallback = new SimpleSwipeCallback(swipeDirections, callback);
    }

    public void setLeaveBehindSwipeLeft(Drawable drawableLeft) {
        setDefaultSwipeDirs(super.getSwipeDirs(null, null) | ItemTouchHelper.LEFT);
        simpleSwipeCallback.setLeaveBehindSwipeLeft(drawableLeft);
    }

    public void setLeaveBehindSwipeRight(Drawable d) {
        setDefaultSwipeDirs(super.getSwipeDirs(null, null) | ItemTouchHelper.RIGHT);
        simpleSwipeCallback.setLeaveBehindSwipeRight(d);
    }

    public void setHorizontalMarginDp(Context ctx, int dp) {
        simpleSwipeCallback.setHorizontalMarginDp(ctx, dp);
    }

    public void setHorizontalMarginPx(int px) {
        simpleSwipeCallback.setHorizontalMarginPx(px);
    }

    public void setBackgroundSwipeLeft(@ColorInt int bgColor) {
        simpleSwipeCallback.setBackgroundSwipeLeft(bgColor);
    }

    public void setBackgroundSwipeRight(@ColorInt int bgColor) {
        simpleSwipeCallback.setBackgroundSwipeRight(bgColor);
    }

    @Override
    public void onSwiped(ViewHolder viewHolder, int direction) {
        simpleSwipeCallback.onSwiped(viewHolder, direction);
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, ViewHolder viewHolder) {
        return simpleSwipeCallback.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        simpleSwipeCallback.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
