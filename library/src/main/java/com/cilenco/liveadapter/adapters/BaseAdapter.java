package com.cilenco.liveadapter.adapters;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

import com.cilenco.liveadapter.model.IViewItem;
import com.cilenco.liveadapter.utils.ItemComparator;
import com.cilenco.liveadapter.utils.UiUtils;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public abstract class BaseAdapter<T extends IViewItem, VH extends BaseAdapter.SimpleViewHolder> extends Adapter<VH> implements LifecycleObserver {
    private static final String KEY_SORT_ORDER = "sort_order";
    private static final String KEY_SELECTION_MODE = "selection_mode";
    private static final String KEY_SELECT_ON_CLICK = "select_on_click";
    private static final String KEY_SELECT_ON_LONG_CLICK = "select_on_long_click";

    private static final String KEY_COMPARATOR = "comparator";

    public interface OnItemClickListener<T> {
        default void onItemClicked(View v, T item, int position) {};
        default boolean onItemLongClicked(View v, T item, int position) { return false; };
    }

    @Retention(SOURCE) @IntDef({SINGLE, MULTIPLE})
    private @interface SelectionMode {}

    @Retention(SOURCE) @IntDef({ASC, DSC})
    private @interface SortOrder {}

    public static final int SINGLE = 0;
    public static final int MULTIPLE = 1;

    public static final int ASC = 1;
    public static final int DSC = -1;

    private RecyclerView recyclerView;

    protected final ItemHolder<T> itemHolder;
    private final SortedList<T> visibleItemList;
    private ArrayList<T> selectedItemList;

    private OnItemClickListener<T> onClickListener;

    private ItemComparator<T> comparator;

    private @SortOrder int sortOrder;
    private @SelectionMode int selectionMode;

    private boolean selectOnClick;
    private boolean selectOnLongClick;

    public BaseAdapter(LifecycleOwner lcOwner, ObservableList<T> items, Class<T> itemClass) {
        itemHolder = new ItemHolder<>(this, items);
        selectedItemList = new ArrayList<>();

        visibleItemList = new SortedList<>(itemClass, new SortedList.Callback<T>() {
            @Override
            public int compare(T item1, T item2) {
                if(comparator == null) {
                    ObservableList<T> items = itemHolder.getItems();
                    return sortOrder * (items.indexOf(item1) - items.indexOf(item2));
                }

                return sortOrder * comparator.compare(item1, item2);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public boolean areContentsTheSame(T item1, T item2) {
                return BaseAdapter.this.areItemContentsTheSame(item1, item2);
            }

            @Override
            public boolean areItemsTheSame(T item1, T item2) {
                return BaseAdapter.this.areItemsTheSame(item1, item2);
            }
        });

        lcOwner.getLifecycle().addObserver(itemHolder);
        lcOwner.getLifecycle().addObserver(this);

        setHasStableIds(true);

        selectionMode = SINGLE;
        sortOrder = ASC;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {
        this.onClickListener = null;
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState == null) return;

        sortOrder = savedInstanceState.getInt(KEY_SORT_ORDER, ASC);
        selectionMode = savedInstanceState.getInt(KEY_SELECTION_MODE, SINGLE);

        selectOnClick = savedInstanceState.getBoolean(KEY_SELECT_ON_CLICK, false);
        selectOnLongClick = savedInstanceState.getBoolean(KEY_SELECT_ON_LONG_CLICK, false);

        comparator = (ItemComparator<T>) savedInstanceState.getSerializable(KEY_COMPARATOR);
    }

    public void saveInstanceState(Bundle outState) {
        if (outState == null) return;

        outState.putInt(KEY_SORT_ORDER, sortOrder);
        outState.putInt(KEY_SELECTION_MODE, selectionMode);

        outState.putBoolean(KEY_SELECT_ON_CLICK, selectOnClick);
        outState.putBoolean(KEY_SELECT_ON_LONG_CLICK, selectOnLongClick);

        outState.putSerializable(KEY_COMPARATOR, comparator);
        //outState.putLongArray("", );
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getIdentifier();
    }

    @Override
    public int getItemCount() {
        return visibleItemList.size();
    }

    public void setSortOrder(@SortOrder int sortOrder) {
        if(this.sortOrder == sortOrder) return;
        this.sortOrder = sortOrder;

        visibleItemList.clear();
        itemHolder.filter();
    }

    public @SortOrder int getSortOrder() {
        return sortOrder;
    }

    /** Sets the comparator which is used to order the items in the list, depending on the
     ** {@link BaseAdapter#getSortOrder()}. Pass null to use the insert order of the item list.
     ** @param comparator Comparator for ordering or null for list insert order */
    public void setComparator(ItemComparator<T> comparator) {
        this.comparator = comparator;
    }

    /** Returns the comparator which is used for ordering of the items in the list.
     ** @return The comparator used for ordering or null for list insert order */
    public ItemComparator<T> getComparator() {
        return comparator;
    }

    /** Scrolls to the specified adapter position. Actual position of the
     ** item on the screen depends on the LayoutManager implementation.
     ** @param position Scroll to this adapter position */
    public void scrollToPosition(int position) {
        if(recyclerView == null) return;

        LayoutManager lm = recyclerView.getLayoutManager();
        if(lm != null) lm.scrollToPosition(position);
    }

    /** Sets the item selection mode. In {@link BaseAdapter#SINGLE} mode only one item can be selected
     ** at the same time whereas {@link BaseAdapter#MULTIPLE} mode can select any number of items.
     ** @param selectionMode {@link BaseAdapter#SINGLE} for single selection or
     ** {@link BaseAdapter#MULTIPLE} for any number selection.*/
    public void setSelectionMode(@SelectionMode int selectionMode) {
        if(this.selectionMode == selectionMode) return;

        this.selectionMode = selectionMode;
        selectedItemList.clear();
    }

    /** Returns the current item selection mode. In {@link BaseAdapter#SINGLE} mode only one item can be
     ** selected at the same time whereas {@link BaseAdapter#MULTIPLE} mode can select any number of items.
     ** @return {@link BaseAdapter#SINGLE} or {@link BaseAdapter#MULTIPLE} */
    public @SelectionMode int getSelectionMode() {
        return selectionMode;
    }

    public boolean getSelectOnClick() {
        return selectOnClick;
    }

    public void setSelectOnClick(boolean selectOnClick) {
        this.selectOnClick = selectOnClick;
    }

    public boolean getSelectOnLongClick() {
        return selectOnLongClick;
    }

    public void setSelectOnLongClick(boolean selectOnLongClick) {
        this.selectOnLongClick = selectOnLongClick;
    }

    public List<T> getSelectedItems() {
        return selectedItemList;
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.onClickListener = listener;
    }

    public void setFilterPredicate(ItemHolder.Predicate<T> predicate) {
        itemHolder.setPredicate(predicate);
    }

    public T getItem(int position) {
        return visibleItemList.get(position);
    }

    public void filter(CharSequence query) {
        itemHolder.filter(query);
    }

    final void setVisibleItems(Set<T> items) {
        visibleItemList.beginBatchedUpdates();

        for (int i = visibleItemList.size() - 1; i >= 0; i--) {
            final T item = visibleItemList.get(i);

            if(items.contains(item)) continue;
            visibleItemList.remove(item);
        }

        visibleItemList.addAll(items);
        visibleItemList.endBatchedUpdates();
    }

    @Override @CallSuper
    public void onBindViewHolder(@NonNull VH holder, int position) {
        T item = getItem(position);
        holder.itemView.setSelected(item.isSelected());
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }

    protected abstract boolean areItemsTheSame(T item1, T item2);
    protected abstract boolean areItemContentsTheSame(T item1, T item2);
    protected abstract int getLayoutIdForPosition(int position);

    public abstract class SimpleViewHolder extends ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public SimpleViewHolder(View itemView) {
            super(itemView);

            Drawable bg = UiUtils.getSelectableBackground(itemView.getContext(), true);
            ViewCompat.setBackground(itemView, bg); // Sets dynamic drawable as background

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position == RecyclerView.NO_POSITION) return;

            T item = getItem(position);
            if(selectOnClick) setSelection(item);

            if(onClickListener != null) onClickListener.onItemClicked(view, item, position);
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            if(position == RecyclerView.NO_POSITION) return false;

            T item = getItem(position);
            if(selectOnLongClick) setSelection(item);

            //noinspection SimplifiableIfStatement
            if(onClickListener == null) return false;

            return onClickListener.onItemLongClicked(view, item, position);
        }

        protected void setSelection(T item) {
            if(!item.isSelectable()) return;

            boolean newSelectionState = !item.isSelected();
            itemView.setSelected(newSelectionState);
            item.setSelected(newSelectionState);

            if(newSelectionState) {
                if(selectionMode == SINGLE) {
                    if(selectedItemList.size() > 0) {
                        T currentSelection = selectedItemList.get(0);
                    }


                    selectedItemList.add(item);
                } else { // selectionMode == MULTIPLE
                    selectedItemList.add(item);
                }
            } else { // item not selected
                selectedItemList.remove(item);
            }
        }
    }
}
