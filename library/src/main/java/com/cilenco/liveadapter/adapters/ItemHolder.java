package com.cilenco.liveadapter.adapters;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableList;
import android.databinding.ObservableList.OnListChangedCallback;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Filter;

import com.cilenco.liveadapter.utils.IViewItem;

import java.util.HashSet;
import java.util.Set;

public class ItemHolder<V extends IViewItem<V>> extends Filter implements LifecycleObserver {
    private BaseAdapter<V,?> adapter;
    private Predicate<V> predicate;

    private ObservableList<V> originalItems;
    private ItemListener<ObservableList<V>> listener;

    private CharSequence lastQuery;

    public interface Predicate<V> {
        boolean keepItem(V item, CharSequence query);
    }

    public ItemHolder(BaseAdapter<V,?> adapter, ObservableList<V> items) {
        this.adapter = adapter;
        originalItems = items;

        listener = new ItemListener<>();

        originalItems.addOnListChangedCallback(listener);
        publishResults(lastQuery, performFiltering(lastQuery));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onStart() {
        originalItems.addOnListChangedCallback(listener);
        publishResults(lastQuery, performFiltering(lastQuery));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onStop() {
        Log.d("SimpleAdapter", "ON_STOP");
        originalItems.removeOnListChangedCallback(listener);
    }

    public ObservableList<V> getItems() {
        return originalItems;
    }

    public void setPredicate(Predicate<V> predicate) {
        this.predicate = predicate;
    }

    public void filter() {
        this.filter(lastQuery);
    }

    @Override
    protected FilterResults performFiltering(CharSequence query) {
        lastQuery = query;

        FilterResults results = new FilterResults();
        Set<V> filteredItems = new HashSet<>();

        if (!TextUtils.isEmpty(lastQuery) && predicate != null) {
            for (V item : originalItems) {
                if (predicate.keepItem(item, query)) {
                    filteredItems.add(item);
                }
            }
        } else {
            filteredItems.addAll(originalItems);
        }

        results.values = filteredItems;
        results.count = filteredItems.size();

        return results;
    }

    @Override @SuppressWarnings("unchecked")
    protected void publishResults(CharSequence constraint, FilterResults results) {
        Set<V> filteredItems = (Set<V>) results.values;
        adapter.setVisibleItems(filteredItems);
    }

    private class ItemListener<T extends ObservableList<V>> extends OnListChangedCallback<T> {
        @Override
        public void onChanged(T sender) {
            publishResults(lastQuery, performFiltering(lastQuery));
        }

        @Override
        public void onItemRangeChanged(T sender, int positionStart, int itemCount) {
            publishResults(lastQuery, performFiltering(lastQuery));
        }

        @Override
        public void onItemRangeInserted(T sender, int positionStart, int itemCount) {
            publishResults(lastQuery, performFiltering(lastQuery));
        }

        @Override
        public void onItemRangeMoved(T sender, int positionStart, int toPosition, int itemCount) {
            publishResults(lastQuery, performFiltering(lastQuery));
        }

        @Override
        public void onItemRangeRemoved(T sender, int positionStart, int itemCount) {
            publishResults(lastQuery, performFiltering(lastQuery));
        }
    }
}
