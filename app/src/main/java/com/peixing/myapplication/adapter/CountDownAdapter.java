package com.peixing.myapplication.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.peixing.myapplication.R;
import com.peixing.myapplication.utils.TimeTools;

import java.util.ArrayList;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class CountDownAdapter extends RecyclerView.Adapter<CountDownAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Long> countDowns;
    private SparseArray<CountDownTimer> countDownMap;

    public CountDownAdapter(Context context, ArrayList<Long> countDowns) {
        this.context = context;
        this.countDowns = countDowns;
        countDownMap = new SparseArray<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_count_down, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        //Here you can fill your row view
        long time = countDowns.get(position);
        time = time - System.currentTimeMillis();

        if (holder.countDownTimer != null) {
            holder.countDownTimer.cancel();
        }
        if (time > 0) {
            //倒计时持续
            holder.countDownTimer = new CountDownTimer(time, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String times = TimeTools.getCountTimeByLong(millisUntilFinished);
                    holder.txtCountDown.setText(times);
//                    holder.txtCountDown.setTime(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    holder.txtCountDown.setText("00:00:00:00");
//                    holder.txtCountDown.setText("00:00:00:00");
                }
            }.start();
            countDownMap.put(holder.txtCountDown.hashCode(), holder.countDownTimer);
        } else {
            //倒计时结束
            holder.txtCountDown.setText("00:00:00:00");
//            holder.txtCountDown.setText("00:00:00:00");
        }
    }

    @Override
    public int getItemCount() {
        return countDowns != null ? countDowns.size() : 0;
    }

    /**
     * 清空资源
     */
    public void cancelAllTimers() {
        if (countDownMap == null) {
            return;
        }
        Log.e("TAG", "size :  " + countDownMap.size());
        for (int i = 0, length = countDownMap.size(); i < length; i++) {
            CountDownTimer cdt = countDownMap.get(countDownMap.keyAt(i));
            if (cdt != null) {
                cdt.cancel();
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCountDown;
        public CountDownTimer countDownTimer;

        public ViewHolder(View itemView) {
            super(itemView);
            txtCountDown = (TextView) itemView.findViewById(R.id.txt_count_down);
        }
    }
}
