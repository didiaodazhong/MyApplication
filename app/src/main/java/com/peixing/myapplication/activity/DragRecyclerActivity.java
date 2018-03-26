package com.peixing.myapplication.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.peixing.myapplication.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

public class DragRecyclerActivity extends AppCompatActivity {
    private SwipeMenuRecyclerView swipeDragRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_recycler);
        swipeDragRecycler = (SwipeMenuRecyclerView) findViewById(R.id.swipe_drag_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        swipeDragRecycler.setLayoutManager(gridLayoutManager);
        swipeDragRecycler.setHasFixedSize(true);
        swipeDragRecycler.setLongPressDragEnabled(true);

        swipeDragRecycler.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {

                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {

            }
        });
    }
}
