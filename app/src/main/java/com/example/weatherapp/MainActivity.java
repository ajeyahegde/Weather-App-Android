package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
MainActivity of the App, gets the Weather Data for location Los Angeles from NodeJS backend
Display the Progress Bar till the data is retrieved
Volley is used for Asynchronous API Calls to Weather API
SharedPreferences is used to store the data, to be used in Fragments
*/
public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBarStart;
    private TextView initText;
    protected RequestQueue queue;
    public String city;
    public String country;
    public String state;
    public double lat;
    public double lng;
    SharedPreferences sharedPreferences;

    JSONArray weatherData;
    JSONObject currentData;
    JSONArray daysData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBarStart = findViewById(R.id.progressBarStart);
        initText = findViewById(R.id.initText);
        progressBarStart.setVisibility(View.VISIBLE);
        initText.setVisibility(View.VISIBLE);
        sharedPreferences = getSharedPreferences("myFavorites", Context.MODE_PRIVATE);
        queue = Volley.newRequestQueue(this);
        getIPInfo();

    }

    //Function to get current Location Coordinate Value and call the getWeatherInfo function to get data
    private void getIPInfo(){
        String url = "https://ipinfo.io/json?token=878e96704f64a8";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("IPInfo Output:",response.toString());
                        try {
                            String[] location = response.getString("loc").split(",");
                            double lat = Double.parseDouble(location[0]);
                            double lng = Double.parseDouble(location[1]);
                            city = response.getString("city");
                            state = response.getString("region");
                            country = response.getString("country");
                            Log.d("City",city);
                            Log.d("lat",String.valueOf(lat));
                            Log.d("lng",String.valueOf(lng));
                            Log.d("Region",state);
                            getWeatherInfo(lat,lng);
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

    //Function to get Weather Data from NodeJS backend(Calls tomorrow.io API to get Weather Data)
    //After getting the data intent is set to FavoriteActivity
    private void getWeatherInfo(double lat,double lng){
        String weatherurl="https://weatherapp-nodejs-backend.ue.r.appspot.com/weather?";
        String location = "latitude="+String.valueOf(lat)+"&longitude="+String.valueOf(lng);
        weatherurl = weatherurl+location;
        JSONObject params = new JSONObject();
        try{
            params.put("latitude",lat);
            params.put("longitude",lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, weatherurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            weatherData = data.getJSONArray("timelines");
                            currentData = weatherData.getJSONObject(0).getJSONArray("intervals").getJSONObject(0);
                            daysData = weatherData.getJSONObject(1).getJSONArray("intervals");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            JSONObject wdata = new JSONObject();
                            wdata.put("currentData",currentData);
                            wdata.put("daysData",daysData);
                            JSONObject toStoreData = new JSONObject();
                            editor.putString("home",wdata.toString());
                            editor.commit();
                            Intent intent = new Intent(MainActivity.this,FavoriteActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            getWeatherInfo(lat,lng);
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
}