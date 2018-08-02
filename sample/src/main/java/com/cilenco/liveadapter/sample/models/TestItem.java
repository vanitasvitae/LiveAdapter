package com.cilenco.liveadapter.sample.models;

import android.support.annotation.NonNull;

import com.cilenco.liveadapter.model.ViewItem;

import java.util.Comparator;

public class TestItem extends ViewItem {

    private final @NonNull String title;

    public TestItem(@NonNull String title) {
        this.title = title;
    }

    public @NonNull String getTitle() {
        return title;
    }
}
