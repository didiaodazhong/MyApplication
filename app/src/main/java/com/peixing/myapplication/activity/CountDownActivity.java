package com.peixing.myapplication.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.peixing.myapplication.R;
import com.peixing.myapplication.adapter.CountDownAdapter;
import com.peixing.myapplication.utils.TimerUtils;

import java.util.ArrayList;
import java.util.Random;

public class CountDownActivity extends AppCompatActivity {
    private RecyclerView recyclerCountDown;
    private ArrayList<Long> times;
    private CountDownAdapter countDownAdapter;
    private LinearLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);

        parent = (LinearLayout) findViewById(R.id.lin_container);

        recyclerCountDown = (RecyclerView) findViewById(R.id.recycler_count_down);
        recyclerCountDown.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerCountDown.setHasFixedSize(true);
        times = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Random random = new Random();
            times.add(System.currentTimeMillis() + random.nextInt(100) * 86400 * 100);
            Log.i("CountDownActivity", "当前时间--" + System.currentTimeMillis() + "--倒计时事件间隔--" + (random.nextInt(100) * 8640000));
        }
        countDownAdapter = new CountDownAdapter(this, times);
        recyclerCountDown.setAdapter(countDownAdapter);

        //默认样式倒计时每种样式下又对应四种时间的格式
        /**
         * 默认+时间格式1:DEFAULT_STYLE <--> TIME_STYLE_ONE = "HH:mm:ss"
         * */
        TextView tv = TimerUtils.getTimer(TimerUtils.DEFAULT_STYLE, this, 120000000, TimerUtils.TIME_STYLE_ONE, 0)
                .getmDateTv();
        parent.addView(tv);
        setmLayoutParams(tv);
        /**
         * 默认+时间格式2:DEFAULT_STYLE <--> TIME_STYLE_TWO = "HH时mm分ss秒"
         * */
        TextView tv1 = TimerUtils.getTimer(TimerUtils.DEFAULT_STYLE, this, 120000000, TimerUtils.TIME_STYLE_TWO, 0)
                .getmDateTv();
        parent.addView(tv1);
        setmLayoutParams(tv1);
        /**
         * 默认+时间格式3:DEFAULT_STYLE <--> TIME_STYLE_THREE = "dd天HH时mm分ss秒"
         * */
        TextView tv2 = TimerUtils.getTimer(TimerUtils.DEFAULT_STYLE, this, 120000000, TimerUtils.TIME_STYLE_THREE, 0)
                .getmDateTv();
        parent.addView(tv2);
        setmLayoutParams(tv2);
        /**
         * 默认+时间格式4:DEFAULT_STYLE <--> TIME_STYLE_FOUR = "dd天HH时mm分"
         * */
        TextView tv3 = TimerUtils.getTimer(TimerUtils.DEFAULT_STYLE, this, 120000000, TimerUtils.TIME_STYLE_FOUR, 0)
                .getmDateTv();
        parent.addView(tv3);
        setmLayoutParams(tv3);
        //样式一倒计时，就是每块数值和每个间隔分开的样式,每种样式下又对应四种时间的格式
        /**
         * 样式一+时间格式1:JD_STYLE <--> TIME_STYLE_ONE = "HH:mm:ss"
         * */
        TextView tv4 = TimerUtils.getTimer(TimerUtils.JD_STYLE, this, 120000000, TimerUtils.TIME_STYLE_ONE, R.drawable.timer_shape)
                .setTimerPadding(10, 10, 10, 10)//设置内间距
                .setTimerTextColor(Color.BLACK)//设置字体颜色
                .setTimerTextSize(40)//设置字体大小
                .setTimerGapColor(Color.BLACK)//设置间隔的颜色
                .getmDateTv();//拿到TextView对象
        parent.addView(tv4);
        setmLayoutParams(tv4);
        /**
         * 样式一+时间格式2:JD_STYLE <--> TIME_STYLE_TWO = "HH时mm分ss秒"
         * */
        TextView tv5 = TimerUtils.getTimer(TimerUtils.JD_STYLE, this, 120000000, TimerUtils.TIME_STYLE_TWO, R.drawable.timer_shape2)
                .setTimerPadding(10, 10, 10, 10)
                .setTimerTextColor(Color.WHITE)
                .setTimerTextSize(40)
                .setTimerGapColor(Color.BLACK)
                .getmDateTv();
        parent.addView(tv5);
        setmLayoutParams(tv5);
        /**
         * 样式一+时间格式3:JD_STYLE <-->TIME_STYLE_THREE = "dd天HH时mm分ss秒"
         * */
        TextView tv6 = TimerUtils.getTimer(TimerUtils.JD_STYLE, this, 120000000, TimerUtils.TIME_STYLE_THREE, R.drawable.timer_shape2)
                .setTimerPadding(10, 10, 10, 10)
                .setTimerTextColor(Color.YELLOW)
                .setTimerTextSize(40)
                .setTimerGapColor(Color.BLACK)
                .getmDateTv();
        parent.addView(tv6);
        setmLayoutParams(tv6);
        /**
         * 样式一+时间格式4:JD_STYLE <-->TIME_STYLE_FOUR = "dd天HH时mm分"
         * */
        TextView tv7 = TimerUtils.getTimer(TimerUtils.JD_STYLE, this, 120000000, TimerUtils.TIME_STYLE_FOUR, R.drawable.timer_shape2)
                .setTimerPadding(15, 15, 15, 15)
                .setTimerTextColor(Color.BLUE)
                .setTimerTextSize(40)
                .setTimerGapColor(Color.BLACK)
                .getmDateTv();
        parent.addView(tv7);
        setmLayoutParams(tv7);


        /**
         * 样式二+时间格式1:VIP_STYLE <-->TIME_STYLE_ONE = "HH:mm:ss"
         * */
        TextView tv8 = TimerUtils.getTimer(TimerUtils.VIP_STYLE, this, 120000000, TimerUtils.TIME_STYLE_ONE, R.drawable.timer_shape)
                .setTimerPadding(15, 15, 15, 15)
                .setTimerTextColor(Color.BLACK)
                .setTimerTextSize(40)
                .setTimerGapColor(Color.BLACK)
                .getmDateTv();
        parent.addView(tv8);
        setmLayoutParams(tv8);

        /**
         * 样式二+时间格式2:VIP_STYLE <-->TIME_STYLE_TWO = "HH时mm分ss秒"
         * */
        TextView tv9 = TimerUtils.getTimer(TimerUtils.VIP_STYLE, this, 120000000, TimerUtils.TIME_STYLE_TWO, R.drawable.timer_shape2)
                .setTimerPadding(15, 15, 15, 15)
                .setTimerTextColor(Color.WHITE)
                .setTimerTextSize(40)
                .setTimerGapColor(Color.BLACK)
                .getmDateTv();
        parent.addView(tv9);
        setmLayoutParams(tv9);
        /**
         * 样式二+时间格式3:VIP_STYLE <-->TIME_STYLE_THREE = "dd天HH时mm分ss秒"
         * */
        TextView tv10 = TimerUtils.getTimer(TimerUtils.VIP_STYLE, this, 120000000, TimerUtils.TIME_STYLE_THREE, R.drawable.timer_shape2)
                .setTimerPadding(15, 15, 15, 15)
                .setTimerTextColor(Color.YELLOW)
                .setTimerTextSize(40)
                .setTimerGapColor(Color.BLACK)
                .getmDateTv();
        parent.addView(tv10);
        setmLayoutParams(tv10);
        /**
         * 样式二+时间格式4:VIP_STYLE <-->TIME_STYLE_FOUR = "dd天HH时mm分"
         * */
        TextView tv11 = TimerUtils.getTimer(TimerUtils.VIP_STYLE, this, 120000000, TimerUtils.TIME_STYLE_FOUR, R.drawable.timer_shape2)
                .setTimerPadding(15, 15, 15, 15)
                .setTimerTextColor(Color.BLUE)
                .setTimerTextSize(40)
                .setTimerGapColor(Color.BLACK)
                .getmDateTv();
        parent.addView(tv11);
        setmLayoutParams(tv11);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownAdapter != null) {
            countDownAdapter.cancelAllTimers();
        }
    }

    private void setmLayoutParams(TextView tv) {
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tv.getLayoutParams();
        params.setMargins(20, 20, 20, 20);
        tv.setLayoutParams(params);
    }
}
