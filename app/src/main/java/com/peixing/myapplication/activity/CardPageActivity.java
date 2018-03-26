package com.peixing.myapplication.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.peixing.myapplication.R;
import com.peixing.myapplication.adapter.CardPageAdapter;

public class CardPageActivity extends AppCompatActivity {
    private HorizontalInfiniteCycleViewPager cyclePager;
    private CardPageAdapter cardPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_page);
        cyclePager = (HorizontalInfiniteCycleViewPager) findViewById(R.id.cycle_pager);
        cardPageAdapter = new CardPageAdapter(this, false);
        cyclePager.setAdapter(cardPageAdapter);

        cyclePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i("CardPageActivity", "onPageScrolled: "+position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.i("CardPageActivity", "onPageSelected: "+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i("CardPageActivity", "onPageScrollStateChanged: "+state);
            }
        });
    }
}
