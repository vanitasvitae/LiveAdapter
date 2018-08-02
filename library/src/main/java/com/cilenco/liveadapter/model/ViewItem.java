package com.cilenco.liveadapter.model;

import java.util.concurrent.atomic.AtomicLong;

public abstract class ViewItem implements IViewItem {
    private static AtomicLong generator = new AtomicLong();

    private final long identifier;
    private boolean selectable;
    private boolean selected;

    private boolean draggable;
    private boolean swipeable;

    public ViewItem() {
        identifier = generator.getAndIncrement();

        selectable = true;
        draggable = true;
        swipeable = true;
    }

    /** Creates a new ViewItem from another one but with its own id.
     * @param other The ViewItem used to create a new instance */
    public ViewItem(ViewItem other) {
        this(); // Create new instance with new id

        this.selectable = other.selectable;

        this.selected = other.selected;
        this.draggable = other.draggable;
        this.swipeable = other.swipeable;
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
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    @Override
    public boolean isDraggable() {
        return draggable;
    }

    @Override
    public void setSwipeable(boolean swipeable) {
        this.swipeable = swipeable;
    }

    @Override
    public boolean isSwipeable() {
        return swipeable;
    }
}
