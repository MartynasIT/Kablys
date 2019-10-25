package com.example.kablys;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class WeatherFragment extends Fragment {

    private ArrayList<WeatherObject> weatherArrayList = new ArrayList<>();
    private ListView listView;
    private TextView temp;
    Context ctx;

    public WeatherFragment() {
    }

    public static URL buildUrlForWeather(int k) {

        String url = null;
        if (k == 0)
        { url = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/231459?apikey=uKL3VAbABHq6mNUGJ91mOMmFwjt6JneS&metric=true";}
       else
        { url = "http://dataservice.accuweather.com/currentconditions/v1/231459?apikey=uKL3VAbABHq6mNUGJ91mOMmFwjt6JneS&details=true";}

        URL wetherUrl = null;
        try {
            wetherUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return wetherUrl;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle SavedInstanceState)
    {
        return inflater.inflate(R.layout.activity_main, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        listView = view.findViewById(R.id.idListView);
        URL weatherUrl = buildUrlForWeather(0);
        new FetchWeatherDetails().execute(weatherUrl);

    }

    private class FetchWeatherDetails extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL weatherUrl = urls[0];
            String weatherSearchResults = null;

            try {
                weatherSearchResults = NetworkUtils.getResponseFromHttpUrl(weatherUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return weatherSearchResults;
        }

        @Override
        protected void onPostExecute(String weatherSearchResults) {
            if(weatherSearchResults != null && !weatherSearchResults.equals("") ) {
                weatherArrayList = JsonFIveDays(weatherSearchResults);
            }


            super.onPostExecute(weatherSearchResults);
        }
    }

    private class WeatherNow extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL weatherUrl = urls[0];
            String weatherSearchResults = null;

            try {
                weatherSearchResults = NetworkUtils.getResponseFromHttpUrl(weatherUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return weatherSearchResults;
        }

        @Override
        protected void onPostExecute(String weatherSearchResults) {
            if(weatherSearchResults != null && !weatherSearchResults.equals("") ) {
            JsonNow(weatherSearchResults);
            }


            super.onPostExecute(weatherSearchResults);
        }
    }


    private ArrayList<WeatherObject> JsonFIveDays(String weatherSearchResults) {
        if(weatherArrayList != null) {
            weatherArrayList.clear();
        }

        if(weatherSearchResults != null) {
            try {
                JSONObject rootObject = new JSONObject(weatherSearchResults);
                JSONArray results = rootObject.getJSONArray("DailyForecasts");

                for (int i = 0; i < results.length(); i++) {
                    WeatherObject weatherObject = new WeatherObject();

                    JSONObject resultsObj = results.getJSONObject(i);

                    String date = resultsObj.getString("Date");
                    date = date.substring(0,10);
                    weatherObject.setDate(date);

                    JSONObject temperatureObj = resultsObj.getJSONObject("Temperature");
                    String minTemperature = temperatureObj.getJSONObject("Minimum").getString("Value");
                    weatherObject.setMinTemp(minTemperature);

                    String maxTemperature = temperatureObj.getJSONObject("Maximum").getString("Value");
                    weatherObject.setMaxTemp(maxTemperature);

                    JSONObject dayObj = resultsObj.getJSONObject("Day");
                    int condition = dayObj.getInt("Icon");
                    weatherObject.setCondition(condition);


                    weatherArrayList.add(weatherObject);
                }

                if(weatherArrayList != null) {
                    WeatherAdapter weatherAdapter = new WeatherAdapter(ctx, weatherArrayList);
                    listView.setAdapter(weatherAdapter);
                }

                return weatherArrayList;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private void JsonNow(String weatherSearchResults) {
        if(weatherArrayList != null) {
            weatherArrayList.clear();
        }

        if(weatherSearchResults != null) {
            try {
                JSONObject rootObject = new JSONObject(weatherSearchResults);
                JSONArray results = rootObject.getJSONArray("DailyForecasts");

                for (int i = 0; i < results.length(); i++) {
                    WeatherObject weatherObject = new WeatherObject();
                    JSONObject resultsObj = results.getJSONObject(i);
                    String date = resultsObj.getString("Date");
                    date = date.substring(0,10);

                    JSONObject temperatureObj = resultsObj.getJSONObject("Temperature");
                    String minTemperature = temperatureObj.getJSONObject("Minimum").getString("Value");
                    String maxTemperature = temperatureObj.getJSONObject("Maximum").getString("Value");

                    JSONObject dayObj = resultsObj.getJSONObject("Day");
                    int condition = dayObj.getInt("Icon");

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}

