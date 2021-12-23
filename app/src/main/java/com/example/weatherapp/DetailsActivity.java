package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
DetailsActivity functionalities:
->Setup Tabs for Displaying Daily Data, Temperature Charts and Second HighChart and provide function to get data for fragments
->Parent Activity for Fragments TodayFragment, WeeklyFragment, WeatherDataFragment
->Setup Twitter Button onClickListener
*/
public class DetailsActivity extends AppCompatActivity {
    JSONObject currentData;
    JSONArray daysData;
    String city;
    String state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        try {
            currentData = new JSONObject(getIntent().getStringExtra("currentData"));
            daysData = new JSONArray(getIntent().getStringExtra("daysData"));
            city = getIntent().getStringExtra("city");
            state = getIntent().getStringExtra("state");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getSupportActionBar().setTitle(city+" , "+state);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TabLayout tabLayout = findViewById(R.id.tabBar);
        TabItem tabToday =  findViewById(R.id.tabToday);
        TabItem tabWeekly = findViewById(R.id.tabWeekly);
        TabItem tabData = findViewById(R.id.tabWeatherData);
        setupTabIcons(tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //Function to setup 3 Tabs for Details Page
    private void setupTabIcons(TabLayout tabs) {
        tabs.getTabAt(0).setIcon(R.drawable.calendar_today);
        tabs.getTabAt(1).setIcon(R.drawable.trending_up);
        tabs.getTabAt(2).setIcon(R.drawable.thermometer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailsmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Function to open Twitter App to tweet Weather Info
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.twitter:
                String tweet;
                String temp ="";
                try {
                    temp = String.valueOf(currentData.getJSONObject("values").getDouble("temperature"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                tweet = "Check out" +city+","+state+" weather! It is "+ temp+" °F!";
                String twitterUrl = "https://twitter.com/intent/tweet?text="+tweet+"&hashtags=CSCI571WeatherForecast";
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl));
                startActivity(viewIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    //Function to provide Days Weather Data to Fragments
    public JSONArray getDaysData(){
        return daysData;
    }
    //Function to provide Current Weather Data to Fragments
    public JSONObject getCurrentData(){
        return currentData;
    }
}