package com.peixing.myapplication.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.peixing.myapplication.R;
import com.peixing.myapplication.view.CircleMenuLayout;

public class CircleActivity extends AppCompatActivity {
    private final static String TAG = "CircleActivity";
    private CircleMenuLayout mCircleMenuLayout;
    private CircleMenuLayout idMenulayout;
    private RelativeLayout idCircleMenuItemCenter;
    private String[] mItemTexts = new String[]{"安全中心 ", "特色服务", "投资理财",
            "转账汇款"}; //"我的账户", "信用卡"
    private int[] mItemImgs = new int[]{
            R.drawable.home_mbank_1_normal,
            R.drawable.home_mbank_2_normal,
            R.drawable.home_mbank_3_normal,
            R.drawable.home_mbank_4_normal};
//    , R.drawable.home_mbank_5_normal,
//    R.drawable.home_mbank_6_normal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);

//        mCircleMenuLayout = (CircleMenuLayout) findViewById(R.id.id_circle_menu_item_center);
        idMenulayout = (CircleMenuLayout) findViewById(R.id.id_menulayout);
        idCircleMenuItemCenter = (RelativeLayout) findViewById(R.id.id_circle_menu_item_center);

        idMenulayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);
        AnimationSet animationSet = new AnimationSet(true);
        final ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.3f, 1.0f, 0.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1800);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.i(TAG, "onAnimationEnd: 动画结束");
//                idMenulayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        scaleAnimation.setFillAfter(true);
        animationSet.addAnimation(scaleAnimation);

        idMenulayout.setOnMenuItemClickListener(new CircleMenuLayout.OnMenuItemClickListener() {

            @Override
            public void itemClick(View view, int pos) {
                Toast.makeText(CircleActivity.this, mItemTexts[pos],
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void itemCenterClick(View view) {
                idMenulayout.setAnimation(scaleAnimation);
               /* Toast.makeText(CircleActivity.this,
                        "you can do something just like ccb  ",
                        Toast.LENGTH_SHORT).show();*/
            }
        });
    }
}
