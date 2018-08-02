package com.cilenco.liveadapter.model;

public interface IViewItem {
    long getIdentifier();

    default void setSelectable(boolean selectable) {}
    default boolean isSelectable() { return false; }

    default void setSelected(boolean selected) {}
    default boolean isSelected() { return false; }

    default void setDraggable(boolean draggable) {}
    default boolean isDraggable() { return false; }

    default void setSwipeable(boolean swipeable) {}
    default boolean isSwipeable() { return false; }
}
