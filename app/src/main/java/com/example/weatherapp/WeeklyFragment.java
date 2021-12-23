package com.example.weatherapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.highsoft.highcharts.common.HIGradient;
import com.highsoft.highcharts.common.HIStop;
import com.highsoft.highcharts.common.hichartsclasses.*;
import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.core.HIChartView;
import com.highsoft.highcharts.core.HIFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeeklyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/*
Fragment to Display Weekly Temperature HighChart in Details Page
 */
public class WeeklyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WeeklyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeeklyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeeklyFragment newInstance(String param1, String param2) {
        WeeklyFragment fragment = new WeeklyFragment();
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

    //Function to set parameters to Temperature HighCharts, gets data from DetailsActivity
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;

        view = inflater.inflate(R.layout.fragment_weekly, container, false);

        DetailsActivity activity = (DetailsActivity) getActivity();

        JSONArray currentDayData= activity.getDaysData();
        HIChartView chartView = view.findViewById(R.id.hc);

        HIOptions options = new HIOptions();

        HIChart chart = new HIChart();
        chart.setType("arearange");
        chart.setZoomType("x");

        options.setChart(chart);

        HITitle title = new HITitle();
        title.setText("Temperature variation by day");
        options.setTitle(title);


        HIXAxis xaxis = new HIXAxis();
        xaxis.setType("datetime");
        ArrayList xAxis = new ArrayList<HIXAxis>(){{add(xaxis);}};
        options.setXAxis(xAxis);

        HIYAxis yaxis = new HIYAxis();
        yaxis.setTitle(new HITitle());
        options.setYAxis(new ArrayList<HIYAxis>(){{add(yaxis);}});

        HITooltip tooltip = new HITooltip();
        tooltip.setShadow(true);
        tooltip.setValueSuffix("°F");
        options.setTooltip(tooltip);

        HILegend legend = new HILegend();
        legend.setEnabled(false);
        options.setLegend(legend);

        HIArearange series = new HIArearange();
        series.setName("Temperatures");

        HISeries hiseries = new HISeries();
        HIPlotOptions plotOptions = new HIPlotOptions();
        HIMarker hiMarker = new HIMarker();
        options.setPlotOptions(plotOptions);
        plotOptions.setSeries(hiseries);
        hiseries.setMarker(hiMarker);
        hiMarker.setFillColor(HIColor.initWithName("orange"));


        //Set Data for HighChart
        ArrayList<Object> data = new ArrayList<>();
        for(int i=0;i<currentDayData.length();i++){
            long epoch=0;
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date dt = format.parse(currentDayData.getJSONObject(i).getString("startTime").substring(0,10));
                    epoch = dt.getTime();
                    System.out.println(epoch);
                }catch (ParseException p) {
                    p.printStackTrace();
                }
                int tempMax = (int)currentDayData.getJSONObject(i).getJSONObject("values").getDouble("temperatureMax");
                int tempMin = (int)currentDayData.getJSONObject(i).getJSONObject("values").getDouble("temperatureMin");
                Object object = new Object[]{epoch,tempMax,tempMin};
                data.add(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        HIStop stop1 = new HIStop(0,HIColor.initWithHexValue("E65C00"));
        HIStop stop2 = new HIStop(1,HIColor.initWithHexValue("CCE0FF"));

        LinkedList<HIStop> list = new LinkedList<>();
        list.add(stop1);
        list.add(stop2);
        HIGradient gradient = new HIGradient(0,0,0,1);
        series.setFillColor(HIColor.initWithLinearGradient(gradient,list));
        series.setData(data);

        ArrayList setSeriesList = new ArrayList<>(Arrays.asList(series));
        options.setSeries(setSeriesList);

        chartView.setOptions(options);

        return view;
    }
}