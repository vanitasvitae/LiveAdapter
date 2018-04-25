package com.cilenco.liveadapter.adapters;

import android.arch.lifecycle.LifecycleOwner;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cilenco.liveadapter.R;
import com.cilenco.liveadapter.utils.IViewItem;

public abstract class TwoLineAdapter<T extends IViewItem<T>> extends SimpleBaseAdapter<T, TwoLineAdapter.TwoLineHolder> {

    public TwoLineAdapter(LifecycleOwner lcOwner, ObservableList<T> items, Class<T> itemClass) {
        super(lcOwner, items, itemClass);
    }

    @Override @NonNull
    public TwoLineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_two_line, parent, false);

        return new TwoLineHolder(itemView);
    }

    protected abstract View createActionView(LayoutInflater inflater, ViewGroup container);

    protected class TwoLineHolder extends BaseAdapter.SimpleViewHolder {
        public TextView title;
        public TextView description;
        public ImageView icon;

        public TwoLineHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            icon = itemView.findViewById(R.id.icon);

            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
            LinearLayout actionHolder = itemView.findViewById(R.id.action_holder);

            View actionView = createActionView(inflater, actionHolder);
            if(actionView == null) return; // No action view for item

            actionHolder.addView(actionView);
            actionView.setOnClickListener(this);
        }
    }
}
