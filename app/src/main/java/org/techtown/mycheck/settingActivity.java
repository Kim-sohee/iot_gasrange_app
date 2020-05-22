package org.techtown.mycheck;

import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class settingActivity extends AppCompatActivity {

    ViewPager viewPager;
    SliderAdapter sliderAdapter;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setTitle("사용설명서");

        viewPager=(ViewPager)findViewById(R.id.viewpager);
        sliderAdapter=new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        tabLayout=(TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager,true);
    }
}
