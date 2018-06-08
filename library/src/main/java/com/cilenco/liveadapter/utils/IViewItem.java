package com.cilenco.liveadapter.utils;

public interface IViewItem<T> extends Comparable<T> {
    long getIdentifier();

    void setSelectable(boolean selectable);

    boolean isSelectable();
    boolean isSelected();
}
