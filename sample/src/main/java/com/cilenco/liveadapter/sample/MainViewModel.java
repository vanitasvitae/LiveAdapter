package com.cilenco.liveadapter.sample;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableArrayList;

import com.cilenco.liveadapter.sample.models.TestItem;

/** ViewModel for {@link MainActivity} */
public class MainViewModel extends ViewModel {
    private ObservableArrayList<TestItem> items;

    public MainViewModel() {
        items = new ObservableArrayList<>();
    }

    public ObservableArrayList<TestItem> getItems() {
        return items;
    }

    public void addItem(TestItem item) {
        items.add(item);
    }

}
