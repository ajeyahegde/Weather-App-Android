package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
//import android.widget.SearchView;
import androidx.appcompat.widget.SearchView;
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

import java.util.ArrayList;
import java.util.HashMap;


/*
HomeActivity key functionalities:
-> Displays Weather Data for Home City
-> Provide onclick functionality to navigate to Details Tab
-> Provide Search functionality in toolbar and Auto-complete option
 */
public class HomeActivity extends AppCompatActivity {
    HashMap<Integer,String> codeStatus;
    HashMap<Integer,String> codeImage;
    JSONObject currentDayData;
    JSONArray daysData;
    String city;
    String state;
    ListView listView;
    ArrayList<String> citiesList;
    protected RequestQueue queue;
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> newsAdapter;
    SearchView.SearchAutoComplete searchAutoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        city = getIntent().getStringExtra("city");
        state = getIntent().getStringExtra("state");
        String location = city+", "+state;
        initialize();
        queue = Volley.newRequestQueue(this);

        try {
            currentDayData = new JSONObject(getIntent().getStringExtra("currentData"));
            daysData = new JSONArray(getIntent().getStringExtra("daysData"));
            System.out.println(currentDayData);
            String time = currentDayData.getString("startTime").substring(0,10).replace('-','/');
            JSONObject currentData = currentDayData.getJSONObject("values");
            int temperature = (int)currentData.getDouble("temperature");
            int maxTemperature = (int)currentData.getDouble("temperatureMin");
            int minTemperature = (int)currentData.getDouble("temperatureMax");
            int humidity = (int)currentData.getDouble("humidity");
            double visibility = currentData.getDouble("visibility");
            int weatherCode = (int)currentData.getDouble("weatherCode");
            double pressure = currentData.getDouble("pressureSeaLevel");
            double windSpeed = currentData.getDouble("windSpeed");
            if(!codeStatus.containsKey(weatherCode))
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
            for(int i=0;i<=13;i++){
                JSONObject dayData = daysData.getJSONObject(i);
                time = dayData.getString("startTime").substring(0,10);
                JSONObject dayValues = dayData.getJSONObject("values");
                int tempMax = (int)dayValues.getDouble("temperatureMax");
                int tempMin = (int)dayValues.getDouble("temperatureMin");
                int dayWeatherCode = (int)dayValues.getInt("weatherCode");
                if(!codeImage.containsKey(dayWeatherCode))
                    dayWeatherCode = 1000;
                String cell1 = "cell"+i+"a";
                String cell2 = "cell"+i+"b";
                String cell3 = "cell"+i+"c";
                String cell4 = "cell"+i+"d";
                int textId1 = getResources().getIdentifier(cell1,"id",getPackageName());
                TextView textView1 = findViewById(textId1);
                textView1.setText(time);


                int textId2 = getResources().getIdentifier(cell2,"id",getPackageName());
                ImageView imageView2 = findViewById(textId2);
                int imgsrc = getResources().getIdentifier(codeImage.get(dayWeatherCode),"drawable",getPackageName());
                imageView2.setImageResource(imgsrc);

                int textId3 = getResources().getIdentifier(cell3,"id",getPackageName());
                TextView textView3 = findViewById(textId3);
                textView3.setText(String.valueOf(tempMin));


                int textId4 = getResources().getIdentifier(cell4,"id",getPackageName());
                TextView textView4 = findViewById(textId4);
                textView4.setText(String.valueOf(tempMax));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CardView cardView = findViewById(R.id.cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,DetailsActivity.class);
                intent.putExtra("currentData",currentDayData.toString());
                intent.putExtra("daysData",daysData.toString());
                intent.putExtra("city",city);
                intent.putExtra("state",state);
                startActivity(intent);
            }
        });
    }

    //Function to implement Search AutoComplete
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchmenu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setIconified(false);
        searchView.setQueryHint("Enter Location");


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setBackgroundColor(getResources().getColor(R.color.black));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.white));
        searchAutoComplete.setDropDownBackgroundResource(R.color.white);


        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
                String query = (String)adapterView.getItemAtPosition(index);
                searchAutoComplete.setText(""+query);
                System.out.println("Item Selected"+query);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println("Select Item: "+query);
                Intent intent = new Intent(HomeActivity.this,SearchableActivity.class);
                intent.putExtra("location",query);
                startActivity(intent);
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                autocomplete(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //Function to call Google Places API for autocomplete functionality to get list of cities
    public void autocomplete(String text){
        citiesList = new ArrayList<>();
        String autocompleteurl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+text+"&key=AIzaSyCS6yPfI8EiGsi6HC3caZMX4zfGiHcFydU&types=(cities)";
        JSONObject params = new JSONObject();
        try {
            params.put("key", "AIzaSyCS6yPfI8EiGsi6HC3caZMX4zfGiHcFydU");
            params.put("types","(cities)");
            params.put("input",text);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, autocompleteurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray predictions = response.getJSONArray("predictions");
                            for(int i=0; i<predictions.length(); i++){
                                citiesList.add(predictions.getJSONObject(i).getString("description"));
                            }
                            System.out.println(citiesList);
                            arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, citiesList);
                            searchAutoComplete.setAdapter(arrayAdapter);
                            arrayAdapter.notifyDataSetChanged();

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

    //Function to initialize HashMap to map WeatherCodes to Status and Display Image
    private void initialize(){
        codeStatus = new HashMap<>();
        codeImage = new HashMap<>();
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