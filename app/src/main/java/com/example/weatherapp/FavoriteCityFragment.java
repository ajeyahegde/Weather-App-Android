package com.example.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteCityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/*Fragment to Display Data to all the Favorites City in the Home Screen
   Key Functionalities:
   ->Display Weather Data for current Favorite City based on Tab number from SharedPreferences
   ->Provide Onclick options to navigate to Details Page
   ->Provide Onclick option to delete the current City from Favorites
 */
public class FavoriteCityFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View view;
    int index = 0;
    SharedPreferences sharedPreferences;
    JSONObject currentDayData;
    JSONArray daysData;
    HashMap<Integer,String> codeStatus;
    HashMap<Integer,String> codeImage;


    public FavoriteCityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoriteCityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoriteCityFragment newInstance(String param1, String param2) {
        FavoriteCityFragment fragment = new FavoriteCityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite_city, container, false);
        initialize();
        index = Integer.parseInt(mParam1);
        sharedPreferences = getActivity().getSharedPreferences("myFavorites", Context.MODE_PRIVATE);
        HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("cities",null);
        ArrayList<String> list = new ArrayList<>(set);
        String city = list.get(index-1);
        String data = sharedPreferences.getString(city, null);
        FloatingActionButton favbutton = view.findViewById(R.id.favoritesButton);
        String time;
        CardView cardView = view.findViewById(R.id.cardView);
        if (data != null) {
            try {
                JSONObject jsondata = new JSONObject(data);
                currentDayData = jsondata.getJSONObject("currentData");
                daysData = jsondata.getJSONArray("daysData");
                JSONObject currentData = currentDayData.getJSONObject("values");
                int temperature = (int) currentData.getDouble("temperature");
                int maxTemperature = (int) currentData.getDouble("temperatureMin");
                int minTemperature = (int) currentData.getDouble("temperatureMax");
                int humidity = (int) currentData.getDouble("humidity");
                double visibility = currentData.getDouble("visibility");
                int weatherCode = (int) currentData.getDouble("weatherCode");
                double pressure = currentData.getDouble("pressureSeaLevel");
                double windSpeed = currentData.getDouble("windSpeed");
                if (!codeStatus.containsKey(weatherCode))
                    weatherCode = 1000;
                int resId = getResources().getIdentifier(codeImage.get(weatherCode), "drawable",getActivity().getPackageName());
                ImageView iv = view.findViewById(R.id.imageViewStatus);
                iv.setImageResource(resId);
                TextView temperatureTextView = view.findViewById(R.id.temperature);
                temperatureTextView.setText(String.valueOf(temperature) + "°F");
                TextView statusTextView = view.findViewById(R.id.status);
                statusTextView.setText(codeStatus.get(weatherCode));
                TextView locationTextView = view.findViewById(R.id.location);
                locationTextView.setText(city);
                TextView humidityTextView = view.findViewById(R.id.humidity);
                humidityTextView.setText(String.valueOf(humidity) + "%");
                TextView visibilityTextView = view.findViewById(R.id.visibility);
                visibilityTextView.setText(String.valueOf(visibility) + "mi");
                TextView pressureTextView = view.findViewById(R.id.pressure);
                pressureTextView.setText(String.valueOf(pressure) + "inHg");
                TextView windspeedTextView = view.findViewById(R.id.windSpeed);
                windspeedTextView.setText(String.valueOf(windSpeed) + "mph");

                //Extracting Days Data
                for (int i = 0; i <= 10; i++) {
                    JSONObject dayData = daysData.getJSONObject(i);
                    time = dayData.getString("startTime").substring(0, 10);
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
                    int textId1 = getResources().getIdentifier(cell1, "id", getActivity().getPackageName());
                    TextView textView1 = view.findViewById(textId1);
                    textView1.setText(time);


                    int textId2 = getResources().getIdentifier(cell2, "id", getActivity().getPackageName());
                    ImageView imageView2 = view.findViewById(textId2);
                    int imgsrc = getResources().getIdentifier(codeImage.get(dayWeatherCode), "drawable", getActivity().getPackageName());
                    imageView2.setImageResource(imgsrc);

                    int textId3 = getResources().getIdentifier(cell3, "id", getActivity().getPackageName());
                    TextView textView3 = view.findViewById(textId3);
                    textView3.setText(String.valueOf(tempMin));


                    int textId4 = getResources().getIdentifier(cell4, "id", getActivity().getPackageName());
                    TextView textView4 = view.findViewById(textId4);
                    textView4.setText(String.valueOf(tempMax));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        favbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set.remove(city);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putStringSet("cities",set);
                editor.remove(city);
                editor.commit();
                Toast.makeText(getActivity().getApplicationContext(),city +" was removed from favorites",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),DetailsActivity.class);
                intent.putExtra("currentData",currentDayData.toString());
                intent.putExtra("daysData",daysData.toString());
                intent.putExtra("city","Los Angeles");
                intent.putExtra("state","California");
                startActivity(intent);
            }
        });



        return view;
    }

    //Function to initialize HashMaps to get Status and Image to Display from WeatherCode
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