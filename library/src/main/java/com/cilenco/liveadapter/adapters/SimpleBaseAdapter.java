package com.cilenco.liveadapter.adapters;

import android.arch.lifecycle.LifecycleOwner;
import android.databinding.ObservableList;

import com.cilenco.liveadapter.utils.IViewItem;

public abstract class SimpleBaseAdapter<T extends IViewItem<T>, VH extends BaseAdapter.SimpleViewHolder> extends BaseAdapter<T, VH> {

    public SimpleBaseAdapter(LifecycleOwner lcOwner, ObservableList<T> items, Class<T> itemClass) {
        super(lcOwner, items, itemClass);
    }

    @Override
    protected int compare(T item1, T item2) {
        return item1.compareTo(item2);
    }

    @Override
    protected boolean areItemsTheSame(T item1, T item2) {
        return item1 == item2;
    }

    @Override
    protected boolean areItemContentsTheSame(T item1, T item2) {
        return item1.equals(item2);
    }
}
