package com.cilenco.liveadapter.sample;

import android.arch.lifecycle.LifecycleOwner;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cilenco.liveadapter.adapters.OneLineAdapter;
import com.cilenco.liveadapter.sample.models.TestItem;

public class TestItemAdapter extends OneLineAdapter<TestItem> {

    public TestItemAdapter(LifecycleOwner lcOwner, ObservableList<TestItem> items) {
        super(lcOwner, items, TestItem.class);
    }

    @Override
    protected View createActionView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull OneLineAdapter.OneLineHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        TestItem item = getItem(position);
        holder.title.setText(item.getTitle());
    }
}
