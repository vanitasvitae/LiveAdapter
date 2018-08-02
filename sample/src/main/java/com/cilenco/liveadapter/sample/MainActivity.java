package com.cilenco.liveadapter.sample;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.cilenco.liveadapter.adapters.BaseAdapter;
import com.cilenco.liveadapter.model.IViewItem;
import com.cilenco.liveadapter.sample.models.TestItem;
import com.cilenco.liveadapter.utils.SimpleDragCallback;
import com.cilenco.liveadapter.utils.SimpleSwipeCallback;
import com.cilenco.liveadapter.views.BetterRecyclerView;

import java.util.Collections;
import java.util.Comparator;

import static com.cilenco.liveadapter.adapters.BaseAdapter.SINGLE;

public class MainActivity extends AppCompatActivity {
    private TestItemAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel vm = ViewModelProviders.of(this).get(MainViewModel.class);

        if(savedInstanceState == null) {
            for (int i = 1000; i > 0; i--) {
                TestItem item = new TestItem(String.format("%04d", i));
                item.setSelectable(true);
                item.setSwipeable(i % 2 == 0);
                item.setDraggable(true);
                vm.addItem(item);
            }
        }

        BetterRecyclerView rcv = findViewById(R.id.list);
        adapter = new TestItemAdapter(this, vm.getItems());

        SimpleDragCallback dragCallback = new SimpleDragCallback(SimpleDragCallback.ALL, new SimpleDragCallback.ItemDragCallback() {
            @Override
            public boolean itemTouchOnMove(int oldPosition, int newPosition) {
                Collections.swap(vm.getItems(), oldPosition, newPosition);

                return true;
            }
        });

        SimpleSwipeCallback swipeCallback = new SimpleSwipeCallback((pos, dir) -> vm.getItems().remove(pos));
        ItemTouchHelper touchHelper = new ItemTouchHelper(dragCallback);
        touchHelper.attachToRecyclerView(rcv);

        adapter.setSelectOnLongClick(true);
        adapter.setSelectionMode(SINGLE);

        rcv.setAdapter(adapter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }
}
