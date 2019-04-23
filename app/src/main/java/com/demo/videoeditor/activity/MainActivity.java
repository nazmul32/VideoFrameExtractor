package com.demo.videoeditor.activity;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.demo.videoeditor.R;
import com.demo.videoeditor.cut.CutFragment;
import com.demo.videoeditor.trim.TrimAdvancedFragment;
import com.demo.videoeditor.trim.TrimFragment;
import com.demo.videoeditor.trim.TrimSimpleFragment;

public class MainActivity extends AppCompatActivity implements
        TrimFragment.OnTrimFragmentInteractionListener,
        CutFragment.OnCutFragmentInteractionListener,
        TrimSimpleFragment.OnTrimSimpleFragmentInteractionListener,
        TrimAdvancedFragment.OnTrimAdvancedFragmentInteractionListener {
    private TextView tvTrim;
    private TextView tvCut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        final ViewPager viewPager = findViewById(R.id.viewpager);
        VideoFragmentPagerAdapter videoFragmentPagerAdapter = new VideoFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(videoFragmentPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tvTrim.setBackgroundResource(R.drawable.bg_trim_selected);
                    tvTrim.setTextColor(getResources().getColor(R.color.color_white));
                    tvCut.setBackgroundResource(R.drawable.bg_cut_unselected);
                    tvCut.setTextColor(getResources().getColor(R.color.menu_item_selected_color));
                } else {
                    tvTrim.setBackgroundResource(R.drawable.bg_trim_unselected);
                    tvTrim.setTextColor(getResources().getColor(R.color.menu_item_selected_color));
                    tvCut.setBackgroundResource(R.drawable.bg_cut_selected);
                    tvCut.setTextColor(getResources().getColor(R.color.color_white));
                }
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

        tvTrim = findViewById(R.id.tv_trim);
        tvCut = findViewById(R.id.tv_cut);
        tvTrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 1) {
                    viewPager.setCurrentItem(0);
                }
            }
        });
        tvCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 0) {
                    viewPager.setCurrentItem(1);
                }
            }
        });
    }

    @Override
    public void onCutFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTrimFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTrimAdvancedFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTrimSimpleFragmentInteraction(Uri uri) {

    }

    private class VideoFragmentPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 2;

        public VideoFragmentPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TrimFragment.newInstance();
                case 1:
                    return CutFragment.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

}

