package com.cilenco.liveadapter.utils;

import android.databinding.ListChangeRegistry;
import android.databinding.MapChangeRegistry;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableArrayMap;
import android.databinding.ObservableList;
import android.databinding.ObservableList.OnListChangedCallback;
import android.databinding.ObservableMap;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.cilenco.liveadapter.model.IViewItem;

import org.apache.commons.collections4.map.ListOrderedMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

public class ItemList<T extends IViewItem> implements ObservableList<T> {
    private transient ListChangeRegistry mListeners = new ListChangeRegistry();
    ListOrderedMap<Long, T> items;

    ObservableArrayList<T> jsdlkf;

    @Override
    public void addOnListChangedCallback(OnListChangedCallback<? extends ObservableList<T>> callback) {
        mListeners.add(callback);
    }

    @Override
    public void removeOnListChangedCallback(OnListChangedCallback<? extends ObservableList<T>> callback) {
        mListeners.remove(callback);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return null; //items.it;
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(@NonNull T1[] a) {
        return null;
    }

    @Override
    public boolean add(T t) {
        if(items.containsKey(t.getIdentifier())) return false;

        items.put(t.getIdentifier(), t);
        notifyAdd(size() - 1, 1);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return false; //items.remove(null, o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return false; //items.co;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        return addAll(items.size(), c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        boolean changed = false;

        for(T item : c) {
            if(items.containsKey(item.getIdentifier())) continue;
            items.put(index, item.getIdentifier(), item);

            changed = true;
        }

        return changed;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public T get(int index) {
        return items.getValue(index);
    }

    @Override
    public T set(int index, T element) {
        return items.setValue(index, element);
    }

    @Override
    public void add(int index, T element) {
        items.put(index, element.getIdentifier(), element);
    }

    @Override
    public T remove(int index) {
        return items.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return -1; //items.
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0; //items.las;
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator() {
        return null;
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return null;
    }

    @NonNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return null;
    }

    private void notifyAdd(int start, int count) {
        if (mListeners != null) {
            mListeners.notifyInserted(this, start, count);
        }
    }

    private void notifyRemove(int start, int count) {
        if (mListeners != null) {
            mListeners.notifyRemoved(this, start, count);
        }
    }
}
