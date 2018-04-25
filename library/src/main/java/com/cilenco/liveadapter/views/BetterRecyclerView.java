package com.cilenco.liveadapter.views;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BetterRecyclerView extends RecyclerView {
    private View placeholderView;
    private EmptyObserver observer;

    public BetterRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public BetterRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BetterRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, VERTICAL, false);
        DividerItemDecoration decoration = new DividerItemDecoration(context, layoutManager.getOrientation());

        setHasFixedSize(true);
        layoutManager.scrollToPosition(0);
        setLayoutManager(layoutManager);

        observer = new EmptyObserver();
        addItemDecoration(decoration);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if(adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        observer.onChanged();
    }

    @Override
    public void scrollToPosition(int position) {
        getLayoutManager().scrollToPosition(position);
        //super.scrollToPosition(position);
    }

    public void setPlaceholder(View placeholderView) {
        ViewGroup parentView = (ViewGroup) getParent();
        parentView.addView(placeholderView);
    }

    public void setPlaceholder(@LayoutRes int layoutRes) {
        ViewGroup parentView = (ViewGroup) getParent();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        placeholderView = inflater.inflate(layoutRes, parentView, false);

        parentView.addView(placeholderView);
    }

    private class EmptyObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            toggleVisibility();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            toggleVisibility();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            toggleVisibility();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            toggleVisibility();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            toggleVisibility();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            toggleVisibility();
        }

        private void toggleVisibility() {
            Adapter<?> adapter =  getAdapter();

            if(adapter != null && placeholderView != null) {

                if(adapter.getItemCount() == 0) {
                    placeholderView.setVisibility(View.VISIBLE);
                    BetterRecyclerView.this.setVisibility(View.GONE);
                }
                else {
                    placeholderView.setVisibility(View.GONE);
                    BetterRecyclerView.this.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
