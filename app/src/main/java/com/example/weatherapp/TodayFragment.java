package com.example.weatherapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/*
Fragment to Display Today's Data in Details Page
 */
public class TodayFragment extends Fragment {

    HashMap<Integer,String> codeStatus;
    HashMap<Integer,String> codeImage;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TodayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodayFragment newInstance(String param1, String param2) {
        TodayFragment fragment = new TodayFragment();
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
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        DetailsActivity activity = (DetailsActivity) getActivity();
        initialize();
        JSONObject currentDayData= activity.getCurrentData();
        try {
        JSONObject currentData = currentDayData.getJSONObject("values");
        int temperature = (int)currentData.getDouble("temperature");
        float precipitation = (float) currentData.getInt("precipitationProbability");
        int maxTemperature = (int)currentData.getDouble("temperatureMin");
        int minTemperature = (int)currentData.getDouble("temperatureMax");
        int humidity = (int)currentData.getDouble("humidity");
        int cloudCover = currentData.getInt("cloudCover");
        double visibility = currentData.getDouble("visibility");
        int weatherCode = (int)currentData.getDouble("weatherCode");
        double pressure = currentData.getDouble("pressureSeaLevel");
        double windSpeed = currentData.getDouble("windSpeed");
        float uvIndex = (float)currentData.getInt("uvIndex");

        if(!codeStatus.containsKey(weatherCode))
            weatherCode = 1000;
        int resId = getResources().getIdentifier(codeImage.get(weatherCode), "drawable", getActivity().getPackageName());
        ImageView iv = view.findViewById(R.id.imageView6);
        iv.setImageResource(resId);

        TextView windSpeedTextView = view.findViewById(R.id.windSpeed);
        TextView pressureTextView = view.findViewById(R.id.pressure);
        TextView precipitationTextView = view.findViewById(R.id.precipitation);
        TextView temperatureTextView = view.findViewById(R.id.temperature);
        TextView statusTextView =(TextView)  view.findViewById(R.id.Status);
        TextView humidityTextView = (TextView) view.findViewById(R.id.humidity);
        TextView visibilityTextView = (TextView) view.findViewById(R.id.visibility);
        TextView cloudCoverTextView =(TextView)  view.findViewById(R.id.cloudCover);
        TextView ozoneTextView = (TextView) view.findViewById(R.id.ozone);

        String temp = String.valueOf(temperature)+"\u2109";
        temperatureTextView.setText(temp);
        temp = String.valueOf(pressure)+" inHg";
        pressureTextView.setText(temp);
        temp = String.valueOf(windSpeed)+" mph";
        windSpeedTextView.setText(temp);
        temp = String.format("%.2f",precipitation)+"%";
        precipitationTextView.setText(temp);
        temp = String.valueOf(humidity)+"%";
        humidityTextView.setText(temp);
        temp = String.valueOf(visibility)+" mi";
        visibilityTextView.setText(temp);
        temp = String.valueOf(cloudCover)+"%";
        cloudCoverTextView.setText(temp);
        //temp = String.valueOf(uvIndex);
        temp = String.format("%.2f", uvIndex);
        ozoneTextView.setText(temp);
        statusTextView.setText(codeStatus.get(weatherCode));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray daysData = activity.getDaysData();
        return view;
    }

    //Function to initialize Hashmap to map Weather Code to Status and Images
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