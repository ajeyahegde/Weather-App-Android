package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;

/*
SearchableActivity functionalities:
-> Called when a city is Searched from Home Page
-> Get Weather Data using call to Weather API from NodeJS backend
-> Display Weather Data for City Searched
-> Provide option to toggle between adding city to Favorite and Removing from Favorites using SharedPreferences
 */
public class SearchableActivity extends AppCompatActivity {

    JSONArray weatherData;
    JSONObject currentData;
    JSONArray daysData;
    String location;
    JSONObject currentDayData;
    protected RequestQueue queue;
    HashMap<Integer,String> codeStatus;
    HashMap<Integer,String> codeImage;
    FloatingActionButton favbutton;
    boolean isFavorite;
    CardView cardView1;
    CardView cardView2;
    CardView cardView3;
    TextView textView;
    TextView textView1;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        location = getIntent().getStringExtra("location");
        queue = Volley.newRequestQueue(this);
        codeStatus = new HashMap<>();
        codeImage = new HashMap<>();
        sharedPreferences = getSharedPreferences("myFavorites", Context.MODE_PRIVATE);
        getSupportActionBar().setTitle(location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progressBar_cyclic);
        textView = findViewById(R.id.fetching);
        cardView1 = findViewById(R.id.cardView);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);
        textView1 = findViewById(R.id.textView);
        favbutton = findViewById(R.id.favoritesButton);
        cardView1.setVisibility(View.GONE);
        cardView2.setVisibility(View.GONE);
        cardView3.setVisibility(View.GONE);
        textView1.setVisibility(View.GONE);
        favbutton.setVisibility(View.GONE);
        initialize();
        getCoordinates(location);
        if (isFavorite == true)
            favbutton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.map_marker_minus));
        if (isFavorite == false)
            favbutton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.map_marker_plus));
        favbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addtoFavorites();
            }
        });
        CardView cardView = findViewById(R.id.cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchableActivity.this,DetailsActivity.class);
                intent.putExtra("currentData",currentDayData.toString());
                intent.putExtra("daysData",daysData.toString());
                String temp[] = location.split(",");
                intent.putExtra("city",temp[0]);
                intent.putExtra("state",temp[1]);
                startActivity(intent);
            }
        });

    }

    //Function to get Latitude and Longitude values for searched city
    public void getCoordinates(String location){
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="+location+"&key=AIzaSyCS6yPfI8EiGsi6HC3caZMX4zfGiHcFydU";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("results");
                            JSONObject location = data.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                            double latitude = location.getDouble("lat");
                            double longitude = location.getDouble("lng");
                            System.out.println("In get Coordinates:"+latitude+"  |  "+longitude);
                            getWeatherInfo(latitude,longitude);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);
    }

    //Function to get Weather Data from tomorrow.io API from call to NodeJS backend
    private void getWeatherInfo(double lat,double lng){
        String weatherurl="https://weatherapp-nodejs-backend.ue.r.appspot.com/weather?";

        String location = "latitude="+String.valueOf(lat)+"&longitude="+String.valueOf(lng);
        weatherurl = weatherurl+location;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, weatherurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            weatherData = data.getJSONArray("timelines");
                            currentDayData = weatherData.getJSONObject(0).getJSONArray("intervals").getJSONObject(0);
                            daysData = weatherData.getJSONObject(1).getJSONArray("intervals");
                            System.out.println("In get Weather Info:"+currentDayData);
                            setData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getWeatherInfo(lat,lng);
                error.printStackTrace();
            }
        });
        queue.add(request);
    }

    //Function to set Data to various components of the page for Display
    public void setData() {
        try {
            JSONObject currentData = currentDayData.getJSONObject("values");
            int temperature = (int) currentData.getDouble("temperature");
            int humidity = (int) currentData.getDouble("humidity");
            double visibility = currentData.getDouble("visibility");
            int weatherCode = (int) currentData.getDouble("weatherCode");
            double pressure = currentData.getDouble("pressureSeaLevel");
            double windSpeed = currentData.getDouble("windSpeed");
            if (!codeStatus.containsKey(weatherCode))
                weatherCode = 1000;
            int resId = getResources().getIdentifier(codeImage.get(weatherCode), "drawable", getPackageName());
            ImageView iv = findViewById(R.id.imageViewStatus);
            iv.setImageResource(resId);
            TextView temperatureTextView = findViewById(R.id.temperature);
            temperatureTextView.setText(String.valueOf(temperature)+"°F");
            TextView statusTextView = findViewById(R.id.status);
            statusTextView.setText(codeStatus.get(weatherCode));
            TextView locationTextView = findViewById(R.id.location);
            locationTextView.setText(location);
            TextView humidityTextView = findViewById(R.id.humidity);
            humidityTextView.setText(String.valueOf(humidity)+"%");
            TextView visibilityTextView = findViewById(R.id.visibility);
            visibilityTextView.setText(String.valueOf(visibility)+"mi");
            TextView pressureTextView = findViewById(R.id.pressure);
            pressureTextView.setText(String.valueOf(pressure)+"inHg");
            TextView windspeedTextView = findViewById(R.id.windSpeed);
            windspeedTextView.setText(String.valueOf(windSpeed)+"mph");

            //Extracting Days Data
            for (int i = 0; i <= 10; i++) {
                JSONObject dayData = daysData.getJSONObject(i);
                String time = dayData.getString("startTime").substring(0, 10);
                JSONObject dayValues = dayData.getJSONObject("values");
                int tempMax = (int) dayValues.getDouble("temperatureMax");
                int tempMin = (int) dayValues.getDouble("temperatureMin");
                int dayWeatherCode = (int) dayValues.getInt("weatherCode");
                if (!codeImage.containsKey(dayWeatherCode))
                    dayWeatherCode = 1000;
                String cell1 = "cell" + i + "a";
                String cell2 = "cell" + i + "b";
                String cell3 = "cell" + i + "c";
                String cell4 = "cell" + i + "d";
                int textId1 = getResources().getIdentifier(cell1, "id", getPackageName());
                TextView textView1 = findViewById(textId1);
                textView1.setText(time);


                int textId2 = getResources().getIdentifier(cell2, "id", getPackageName());
                ImageView imageView2 = findViewById(textId2);
                int imgsrc = getResources().getIdentifier(codeImage.get(dayWeatherCode), "drawable", getPackageName());
                imageView2.setImageResource(imgsrc);

                int textId3 = getResources().getIdentifier(cell3, "id", getPackageName());
                TextView textView3 = findViewById(textId3);
                textView3.setText(String.valueOf(tempMin));


                int textId4 = getResources().getIdentifier(cell4, "id", getPackageName());
                TextView textView4 = findViewById(textId4);
                textView4.setText(String.valueOf(tempMax));
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                cardView1.setVisibility(View.VISIBLE);
                cardView2.setVisibility(View.VISIBLE);
                cardView3.setVisibility(View.VISIBLE);
                textView1.setVisibility(View.VISIBLE);
                favbutton.setVisibility(View.VISIBLE);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Function to handle Favorites button: Add or Remove current city from Favorites, SharedPreferences is used
    public void addtoFavorites(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(isFavorite){
            isFavorite = false;
            HashSet<String> citySet = (HashSet<String>) sharedPreferences.getStringSet("cities",new HashSet<String>());
            citySet.remove(location);
            editor.putStringSet("cities",citySet);
            editor.remove(location);
            editor.commit();
            System.out.println("In Fav:---"+citySet);
            favbutton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.map_marker_plus));
            Toast.makeText(getApplicationContext(),location +" was removed from favorites",Toast.LENGTH_SHORT).show();
        }else{
            isFavorite = true;
            HashSet<String> citySet = (HashSet<String>) sharedPreferences.getStringSet("cities",new HashSet<String>());
            citySet.add(location);
            editor.putStringSet("cities",citySet);
            JSONObject wdata = new JSONObject();
            try {
                wdata.put("currentData", currentDayData);
                wdata.put("daysData", daysData);
            }catch (Exception e){
                e.printStackTrace();
            }
            editor.putString(location,wdata.toString());
            System.out.println("In Fav:---"+citySet);
            editor.commit();

            favbutton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.map_marker_minus));
            Toast.makeText(getApplicationContext(),location +" was added to favorites",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SearchableActivity.this,FavoriteActivity.class);
        startActivity(intent);
    }

    //Function to initialize Hashmap to map WeatherCode to status and image to Display
    private void initialize(){
        codeStatus.put(1000,"Clear");
        codeStatus.put(1001,"Cloudy");
        codeStatus.put(1100,"Mostly Clear");
        codeStatus.put(1101,"Partly Cloudy");
        codeStatus.put(1102,"Mostly Cloudy");
        codeStatus.put(2000,"Fog");
        codeStatus.put(2100,"Light Fog");
        codeStatus.put(3000,"Light Wind");
        codeStatus.put(3001,"Wind");
        codeStatus.put(3002,"Strong Wind");
        codeStatus.put(4000,"Drizzle");
        codeStatus.put(4001,"Rain");
        codeStatus.put(4200,"Light Rain");
        codeStatus.put(4201," HeayvRain");
        codeStatus.put(5000,"Snow");
        codeStatus.put(5001,"Flurries");
        codeStatus.put(5100,"Light Snow");
        codeStatus.put(5101,"Heavy Snow");
        codeStatus.put(6000,"Freezing Drizzle");
        codeStatus.put(6001,"Freezing Rain");
        codeStatus.put(6200,"Light Freezing Rain");
        codeStatus.put(6201,"Heavy Freezing Rain");
        codeStatus.put(7000,"Ice Pellets");
        codeStatus.put(7101,"Heavy Ice Pellets");
        codeStatus.put(7102,"Light Ice Pellets");
        codeStatus.put(8000,"Thunderstorm");
        codeImage.put(1000,"ic_clear_day");
        codeImage.put(1001,"ic_cloudy");
        codeImage.put(1100,"ic_mostly_clear_day");
        codeImage.put(1101,"ic_partly_cloudy_day");
        codeImage.put(1102,"ic_mostly_cloudy");
        codeImage.put(2000,"ic_fog");
        codeImage.put(2100,"ic_fog_light");
        codeImage.put(3000,"weather_windy");
        codeImage.put(3001,"weatherwindyvariant");
        codeImage.put(3002,"weather_windy");
        codeImage.put(4000,"ic_drizzle");
        codeImage.put(4001,"ic_rain");
        codeImage.put(4200,"ic_rain_light");
        codeImage.put(4201,"ic_rain_heavy");
        codeImage.put(5000,"ic_snow");
        codeImage.put(5001,"ic_flurries");
        codeImage.put(5100,"ic_snow_light");
        codeImage.put(5101,"ic_snow_heavy");
        codeImage.put(6000,"ic_freezing_drizzle");
        codeImage.put(6001,"ic_freezing_rain");
        codeImage.put(6200,"ic_freezing_rain_light");
        codeImage.put(6201,"ic_freezing_rain_heavy");
        codeImage.put(7000,"ic_ice_pellets");
        codeImage.put(7101,"ic_ice_pellets_heavy");
        codeImage.put(7102,"ic_ice_pellets_light");
        codeImage.put(8000,"ic_tstorm");
    }
}