package com.example.ver.myfirstapp;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Bluetooth", "Wifi", "Internet" };
    private Context context;


    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return bluetooth_ui_activity.newInstance();
            case 1:
                return WiFiDirectUIActivity.newInstance();
            case 2:
                return PageFragment.newInstance();

        }
        return failfrag.newInstance();

            }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}