package com.cilenco.liveadapter.utils;

import java.util.concurrent.atomic.AtomicLong;

public abstract class ViewItem<T> implements IViewItem<T> {
    private static AtomicLong generator = new AtomicLong();

    private final long identifier;
    private boolean selectable;

    public ViewItem() {
        identifier = generator.getAndIncrement();
    }

    public ViewItem(ViewItem other) {
        identifier = other.identifier;
        selectable = other.selectable;
    }

    @Override
    public long getIdentifier() {
        return identifier;
    }

    @Override
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    @Override
    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public boolean isSelected() {
        return false;
    }
}
