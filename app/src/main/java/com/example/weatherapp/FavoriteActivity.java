package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
Favorite Activity functionalities includes:
->Setup ViewPager Adapter for Fragments to display Home Location data and Favorites Data
->Create Search Functionality in App Toolbar
->Setup Auto-complete option to Search the City
*/
public class FavoriteActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    FavoritesAdapter favoritesAdapter;
    protected RequestQueue http_queue;
    protected double lat;
    protected double lon;
    protected String city;
    protected String state;
    protected String country;
    JSONObject currentDayData;
    JSONArray daysData;
    int numOfFragments;
    Set<String> StoredData;
    ArrayList<String> citiesList;
    protected RequestQueue queue;
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> newsAdapter;
    SearchView.SearchAutoComplete searchAutoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        http_queue = Volley.newRequestQueue(this);
        sharedpreferences = getSharedPreferences("myFavorites", Context.MODE_PRIVATE);
        StoredData = sharedpreferences.getStringSet("cities", new HashSet<>());
        queue = Volley.newRequestQueue(this);
        setupAdapter();

    }

    //Function to setup ViewPager for displaying HomeScreen and Favorites
    public void setupAdapter(){
            favoritesAdapter= new FavoritesAdapter(this.getSupportFragmentManager(),this,StoredData);
            ViewPager viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(favoritesAdapter);
            TabLayout tabs = findViewById(R.id.tabs);
            tabs.setupWithViewPager(viewPager);
    }

    //Function to Setup Search Functionality and Search Auto-complete
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
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(FavoriteActivity.this,SearchableActivity.class);
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

    //Function to call Google Places Auto-complete API to get City List taking entered text as input
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

}