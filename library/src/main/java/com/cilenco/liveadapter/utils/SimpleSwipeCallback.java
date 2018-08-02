package com.cilenco.liveadapter.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.View;

import com.cilenco.liveadapter.adapters.BaseAdapter;
import com.cilenco.liveadapter.model.IViewItem;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class SimpleSwipeCallback extends ItemTouchHelper.SimpleCallback {

    public interface ItemSwipeCallback {
        /**
         * Called when an item has been swiped
         *
         * @param position  position of item in the adapter
         * @param direction direction the item was swiped
         */
        void itemSwiped(int position, int direction);
    }

    public static final int ALL = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    public static final int LEFT = ItemTouchHelper.LEFT;
    public static final int RIGHT = ItemTouchHelper.RIGHT;

    @Retention(SOURCE) @IntDef({ALL, LEFT, RIGHT})
    @interface SwipeDirections {}

    @NonNull private final ItemSwipeCallback itemSwipeCallback;
    private boolean isSwipeEnabled = true;

    private int bgColorLeft = Color.TRANSPARENT;
    private int bgColorRight = Color.TRANSPARENT;

    private Drawable leaveBehindDrawableLeft;
    private Drawable leaveBehindDrawableRight;

    private Paint bgPaint;
    private int horizontalMargin = Integer.MAX_VALUE;

    public SimpleSwipeCallback(@NonNull ItemSwipeCallback itemSwipeCallback) {
        this(ALL, itemSwipeCallback);
    }

    public SimpleSwipeCallback(@SwipeDirections int swipeDirs, @NonNull ItemSwipeCallback itemSwipeCallback) {
        super(0, swipeDirs);
        this.itemSwipeCallback = itemSwipeCallback;
    }

    public void setLeaveBehindSwipeLeft(Drawable drawableLeft) {
        this.leaveBehindDrawableLeft = drawableLeft;

        int swipeDirs = super.getSwipeDirs(null, null);
        setDefaultSwipeDirs(swipeDirs | ItemTouchHelper.LEFT);
    }

    public void setLeaveBehindSwipeRight(Drawable drawableRight) {
        this.leaveBehindDrawableRight = drawableRight;

        int swipeDirs = super.getSwipeDirs(null, null);
        setDefaultSwipeDirs(swipeDirs | ItemTouchHelper.RIGHT);
    }

    public void setHorizontalMarginDp(Context ctx, int dp) {
        DisplayMetrics display = ctx.getResources().getDisplayMetrics();
        setHorizontalMarginPx((int) (display.density * dp));
    }

    public void setHorizontalMarginPx(int px) {
        horizontalMargin = px;
    }

    public void setBackgroundSwipeLeft(@ColorInt int bgColor) {
        bgColorLeft = bgColor;
    }

    public void setBackgroundSwipeRight(@ColorInt int bgColor) {
        bgColorRight = bgColor;
    }

    public void setIsSwipeEnabled(boolean isSwipeEnabled) {
        this.isSwipeEnabled = isSwipeEnabled;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return isSwipeEnabled;
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, ViewHolder viewHolder) {
        BaseAdapter adapter = (BaseAdapter) recyclerView.getAdapter();
        IViewItem item = adapter.getItem(viewHolder.getAdapterPosition());

        if(!item.isSwipeable()) return 0; // No direction
        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public void onSwiped(ViewHolder viewHolder, int direction) {
        viewHolder.itemView.setTranslationX(0);
        viewHolder.itemView.setTranslationY(0);

        int position = viewHolder.getAdapterPosition();

        if (position != RecyclerView.NO_POSITION) {
            itemSwipeCallback.itemSwiped(position, direction);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
        return false; // drag disabled
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        if (viewHolder.getAdapterPosition() == RecyclerView.NO_POSITION) {
            return;
        }

        if (Math.abs(dX) > Math.abs(dY)) {
            boolean isLeft = dX < 0;
            if (bgPaint == null) {
                bgPaint = new Paint();
                if (horizontalMargin == Integer.MAX_VALUE) {
                    setHorizontalMarginDp(recyclerView.getContext(), 16);
                }
            }

            bgPaint.setColor(isLeft ? bgColorLeft : bgColorRight);

            if (bgPaint.getColor() != Color.TRANSPARENT) {
                int left = isLeft ? itemView.getRight() + (int) dX : itemView.getLeft();
                int right = isLeft ? itemView.getRight() : (itemView.getLeft() + (int) dX);

                c.drawRect(left, itemView.getTop(), right, itemView.getBottom(), bgPaint);
            }

            Drawable drawable = isLeft ? leaveBehindDrawableLeft : leaveBehindDrawableRight;
            if (drawable != null) {
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = drawable.getIntrinsicWidth();
                int intrinsicHeight = drawable.getIntrinsicWidth();

                int left;
                int right;

                if (isLeft) {
                    left = itemView.getRight() - horizontalMargin - intrinsicWidth;
                    right = itemView.getRight() - horizontalMargin;
                } else {
                    left = itemView.getLeft() + horizontalMargin;
                    right = itemView.getLeft() + horizontalMargin + intrinsicWidth;
                }

                int top = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int bottom = top + intrinsicHeight;

                drawable.setBounds(left, top, right, bottom);
                drawable.draw(c);
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
