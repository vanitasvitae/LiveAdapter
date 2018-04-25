package com.cilenco.liveadapter.adapters;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableList;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

import com.cilenco.liveadapter.utils.IViewItem;

import java.lang.annotation.Retention;
import java.util.Comparator;
import java.util.Set;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public abstract class BaseAdapter<T extends IViewItem<T>, VH extends BaseAdapter.SimpleViewHolder> extends Adapter<VH> implements LifecycleObserver {
    public interface OnItemClickListener<T> {
        void onItemClicked(View v, T item, int position);
    }

    @Retention(SOURCE) @IntDef({ASC, DSC})
    private @interface Order {}

    public static final int ASC = 1;
    public static final int DSC = -1;

    private RecyclerView recyclerView;

    protected final ItemHolder<T> itemHolder;
    private final SortedList<T> visibleItemList;

    private OnItemClickListener<T> onClickListener;

    private int order;
    private Comparator<T> comparator;

    public BaseAdapter(LifecycleOwner lcOwner, ObservableList<T> items, Comparator<T> comparator, Class<T> itemClass) {
        visibleItemList = new SortedList<>(itemClass, new SortedList.Callback<T>() {
            @Override
            public int compare(T item1, T item2) {
                return order * comparator.compare(item1, item2);
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

        itemHolder = new ItemHolder<>(this, items);

        lcOwner.getLifecycle().addObserver(itemHolder);
        lcOwner.getLifecycle().addObserver(this);

        setHasStableIds(true);
        order = ASC;
    }

    public BaseAdapter(LifecycleOwner lcOwner, ObservableList<T> items, Class<T> itemClass) {
        visibleItemList = new SortedList<>(itemClass, new SortedList.Callback<T>() {
            @Override
            public int compare(T item1, T item2) {
                return BaseAdapter.this.compare(item1, item2);
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

        itemHolder = new ItemHolder<>(this, items);

        lcOwner.getLifecycle().addObserver(itemHolder);
        lcOwner.getLifecycle().addObserver(this);

        setHasStableIds(true);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {
        this.onClickListener = null;
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

    public void setOrder(@Order int order) {
        if(this.order == order) return;
        this.order = order;

        visibleItemList.clear();
        itemHolder.filter();
    }

    public @Order int getOrder() {
        return order;
    }

    public void setComparator(@NonNull Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public @NonNull Comparator<T> getComparator() {
        return comparator;
    }

    public void scrollToPosition(int position) {
        if(recyclerView == null) return;
        recyclerView.scrollToPosition(position);
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

    protected abstract int compare(T item1, T item2);
    protected abstract boolean areItemsTheSame(T item1, T item2);
    protected abstract boolean areItemContentsTheSame(T item1, T item2);

    public abstract class SimpleViewHolder extends ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public SimpleViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if(position != RecyclerView.NO_POSITION && onClickListener != null) {
                onClickListener.onItemClicked(view, getItem(position), position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

}
