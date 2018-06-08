package com.cilenco.liveadapter.adapters;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public abstract class OneLineAdapter<T extends IViewItem<T>> extends SimpleBaseAdapter<T, OneLineAdapter.OneLineHolder> {

    public OneLineAdapter(LifecycleOwner lcOwner, ObservableList<T> items, Class<T> itemClass) {
        super(lcOwner, items, itemClass);
    }

    @Override @NonNull
    public OneLineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView = inflater.inflate(R.layout.item_one_line, parent, false);
        return new OneLineHolder(itemView);
    }

    protected abstract View createActionView(LayoutInflater inflater, ViewGroup container);

    protected class OneLineHolder extends BaseAdapter.SimpleViewHolder {
        public TextView title;
        public ImageView icon;

        public OneLineHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
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
