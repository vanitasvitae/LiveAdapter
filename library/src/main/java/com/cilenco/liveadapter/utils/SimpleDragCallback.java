package com.cilenco.liveadapter.utils;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.cilenco.liveadapter.adapters.BaseAdapter;
import com.cilenco.liveadapter.model.IViewItem;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class SimpleDragCallback extends ItemTouchHelper.SimpleCallback {

    public interface ItemDragCallback {

        /**
         * Called when an item has been dragged
         * This event is called on every item in a dragging chain
         *
         * @param oldPosition start position
         * @param newPosition end position
         * @return true if moved otherwise false
         */
        boolean itemTouchOnMove(int oldPosition, int newPosition);

        /**
         * Called when an item has been dropped
         * This event is only called once when the user stopped dragging the item
         *
         * @param oldPosition start position
         * @param newPosition end position
         */
        default void itemTouchDropped(int oldPosition, int newPosition) {

        }
    }

    @NonNull private final ItemDragCallback mCallbackItemTouch;
    private boolean isDragEnabled = true;

    public static final int ALL = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    public static final int UP_DOWN = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    public static final int LEFT_RIGHT = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

    @Retention(SOURCE) @IntDef({ALL, UP_DOWN, LEFT_RIGHT})
    protected @interface DragDirections {}

    private int from = RecyclerView.NO_POSITION;
    private int to = RecyclerView.NO_POSITION;

    public SimpleDragCallback(@NonNull ItemDragCallback itemDragCallback) {
        this(UP_DOWN, itemDragCallback);
    }

    public SimpleDragCallback(@DragDirections int directions, @NonNull ItemDragCallback itemDragCallback) {
        super(directions, 0);
        this.mCallbackItemTouch = itemDragCallback;
    }

    public void setIsDragEnabled(boolean mIsDragEnabled) {
        this.isDragEnabled = mIsDragEnabled;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return isDragEnabled;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
        BaseAdapter adapter = (BaseAdapter) recyclerView.getAdapter();
        IViewItem item = adapter.getItem(viewHolder.getAdapterPosition());

        if(item.isDraggable()) {
            if(from == RecyclerView.NO_POSITION) {
                from = viewHolder.getAdapterPosition();
            }

            to = target.getAdapterPosition();
        }

        return mCallbackItemTouch.itemTouchOnMove(viewHolder.getAdapterPosition(), target.getAdapterPosition()); // information to the interface*/
    }

    @Override
    public int getDragDirs(RecyclerView recyclerView, ViewHolder viewHolder) {
        BaseAdapter adapter = (BaseAdapter) recyclerView.getAdapter();
        IViewItem item = adapter.getItem(viewHolder.getAdapterPosition());

        if(!item.isDraggable()) return 0; // No direction
        return super.getDragDirs(recyclerView, viewHolder);
    }

    @Override
    public void onSwiped(ViewHolder viewHolder, int direction) {
        // swiped disabled
    }

    @Override
    public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (from != RecyclerView.NO_POSITION && to != RecyclerView.NO_POSITION) {
            mCallbackItemTouch.itemTouchDropped(from, to);
        }

        // reset the from / to positions
        from = to = RecyclerView.NO_POSITION;
    }
}
