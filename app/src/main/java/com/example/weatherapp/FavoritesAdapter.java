package com.example.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.Set;

/*
    Java Class to implement Favorites Adapter
    Functionalities include:
    ->Based on position, set the Fragment to HomeScreen or corresponding Favorite City
    ->Get Count dynamically on number of Tabs to be showm
    ->Get current Position of the Tab
*/
public class FavoritesAdapter extends FragmentPagerAdapter {
    Set<String> citySet;
    private long baseId = 0;
    public FavoritesAdapter(@NonNull FragmentManager fm, Context context,Set<String> StoredData) {
        super(fm);
        citySet = StoredData;
    }

    //Function to dynamically set Fragment to Home City of Fragment Cities by passing position of Tab
    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0){
            fragment = HomeCityFragment.newInstance(String.valueOf(position),"dummy");
        }
        else{
            fragment = FavoriteCityFragment.newInstance(String.valueOf(position),"dummy");
        }
        return fragment;
    }

    //Get Count of number of Tabs based on number of Favorites stored
    @Override
    public int getCount() {
        return citySet.size()+1;
    }

    //Get position of current selected Tab
    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public long getItemId(int position) {
        return baseId + position;
    }

    public void notifyChangeInPosition(int n) {
        citySet.remove(n);
        baseId += getCount() + n;
        notifyDataSetChanged();
    }
}
