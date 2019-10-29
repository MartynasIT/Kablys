package com.example.kablys;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView temp, pressure, humidity, fishBite;
    private ImageView WeatherIcon;
    private String pressureTendency;
    private Button more, less;
    private RelativeLayout WeatherNow;
    Context ctx;

    public WeatherFragment() {
    }

    public static URL buildUrlForWeather(int k) {

        String url = null;
        if (k == 0)
        { url = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/231459?apikey=5lwAW3tk5dAxpDUh28dLQZY9QsD8e7tW&metric=true";}
       else
        { url = "http://dataservice.accuweather.com/currentconditions/v1/231459?apikey=5lwAW3tk5dAxpDUh28dLQZY9QsD8e7tW&details=true";}

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
       temp = view.findViewById(R.id.TempNow);
       WeatherIcon = view.findViewById(R.id.conditionToday);
       pressure = view.findViewById(R.id.PressureNow);
        humidity = view.findViewById(R.id.HumidityNow);
       fishBite = view.findViewById(R.id.biteNow);
       view.findViewById(R.id.biteNow).setSelected(true);
        WeatherNow = view.findViewById(R.id.weatherNow);
        URL weatherUrl = buildUrlForWeather(0);
        new FetchWeatherDetails().execute(weatherUrl);
        URL weatherNow = buildUrlForWeather(1);
        new WeatherNow().execute(weatherNow);

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
                JSONArray mJsonArray = new JSONArray(weatherSearchResults);
                JSONObject mJsonObject = mJsonArray.getJSONObject(0);

                JSONObject temperatureObj = mJsonObject.getJSONObject("Temperature");
                String minTemperature = temperatureObj.getJSONObject("Metric").getString("Value");
                temp.setText(minTemperature + " C");

                JSONObject pressureObj = mJsonObject.getJSONObject("Pressure");
                String pressureText = pressureObj.getJSONObject("Imperial").getString("Value");
                pressure.setText(pressureText + " inHg");

                JSONObject pressureTendc = mJsonObject.getJSONObject("PressureTendency");
                pressureTendency = pressureTendc.getString("LocalizedText");
                String Humidity = mJsonObject.getString("RelativeHumidity");
                humidity.setText(Humidity + " %");


                double pressure = Double.parseDouble(pressureText);
                String bite;
                if (pressure >= 30.50 && pressureTendency.equals("Steady"))
                {
                    bite = "Prognozė: Vidutinis arba lėtas kibimas, dėl aukšto slėgio. Žvejok kantriai giliame vandenyje";
                    fishBite.setText(bite);
                    fishBite.setTextColor(Color.YELLOW);
                }

                else if (pressureTendency.equals("Rising") && pressure < 30.50)
                {
                    bite = "Prognozė: Žuvys atkyvėja dėl augančio slėgio, bet aktyvumas dugną mėgstančių žuvų mažėja";
                    fishBite.setText(bite);
                    fishBite.setTextColor(Color.GREEN);
                }

                else if (pressureTendency.equals("Falling") && pressure <= 30.50  &&  pressure > 29.60 )
                {
                    bite = "Prognozė: Žuvų aktyvumas mažėja, dėl krentančio slėgio. Bet yra išimčių kai kurioms plėšrių žuvų rūšims, kaip šamai, lydekos";
                    fishBite.setText(bite);
                    fishBite.setTextColor(Color.YELLOW);
                }

               else if (pressure >= 29.70 && pressure <= 30.40 && pressureTendency.equals("Steady"))
                {
                    bite = "Prognozė: Normalus kibimas, pabandytk įvairius jaukus ir technikas";
                    fishBite.setText(bite);
                    fishBite.setTextColor(Color.GREEN);
                }

                else if (pressure <= 29.60 && pressureTendency.equals("Steady"))
                {
                    bite = "Prognozė: Lėtas kibimas, dėl žemo slėgio, reiktu žvejoti giliai ir lėtai";
                    fishBite.setText(bite);
                    fishBite.setTextColor(Color.RED);
                }

                else
                {
                    bite = "Neaiškus kibimas";
                    fishBite.setText(bite);

                }




               int conditionNR = mJsonObject.getInt("WeatherIcon");
                switch (conditionNR) {
                    case 1:
                        WeatherIcon.setImageResource(R.drawable.vienas);
                        break;

                    case 2:
                        WeatherIcon.setImageResource(R.drawable.du);
                        break;
                    case 3:
                        WeatherIcon.setImageResource(R.drawable.trys);
                        break;
                    case 4:
                        WeatherIcon.setImageResource(R.drawable.keturi);
                        break;
                    case 5:
                        WeatherIcon.setImageResource(R.drawable.penki);
                        break;
                    case 6:
                        WeatherIcon.setImageResource(R.drawable.sesi);
                        break;
                    case 7:
                        WeatherIcon.setImageResource(R.drawable.septyni);
                        break;

                    case 8:
                        WeatherIcon.setImageResource(R.drawable.astuoni);
                        break;
                    case 11:
                        WeatherIcon.setImageResource(R.drawable.vienolika);
                        break;
                    case 12:
                        WeatherIcon.setImageResource(R.drawable.dvylika);
                        break;
                    case 13:
                        WeatherIcon.setImageResource(R.drawable.trylika);
                        break;
                    case 14:
                        WeatherIcon.setImageResource(R.drawable.keturiolika);
                        break;

                    case 15:
                        WeatherIcon.setImageResource(R.drawable.penkiolika);
                        break;
                    case 16:
                        WeatherIcon.setImageResource(R.drawable.sesiolika);
                        break;
                    case 17:
                        WeatherIcon.setImageResource(R.drawable.septyniolika);
                        break;
                    case 18:
                        WeatherIcon.setImageResource(R.drawable.astuoniolika);
                        break;
                    case 19:
                        WeatherIcon.setImageResource(R.drawable.devyniolika);
                        break;
                    case 20:
                        WeatherIcon.setImageResource(R.drawable.dvidesimt);
                        break;
                    case 21:
                        WeatherIcon.setImageResource(R.drawable.dvidesimtvienas);
                        break;
                    case 22:
                        WeatherIcon.setImageResource(R.drawable.dvidesimtdu);
                        break;
                    case 23:
                        WeatherIcon.setImageResource(R.drawable.dvidesimttrys);
                        break;
                    case 24:
                        WeatherIcon.setImageResource(R.drawable.dvidesimtketuri);
                        break;
                    case 25:
                        WeatherIcon.setImageResource(R.drawable.dvidesimtpenki);
                        break;
                    case 26:
                        WeatherIcon.setImageResource(R.drawable.dvidesimtsesi);
                        break;
                    case 27:
                        WeatherIcon.setImageResource(R.drawable.dvidesimtdevyni);
                        break;
                    case 29:
                        WeatherIcon.setImageResource(R.drawable.dvidesimtdevyni);
                        break;
                    case 31:
                        WeatherIcon.setImageResource(R.drawable.tridesimtvienas);
                        break;
                    case 32:
                        WeatherIcon.setImageResource(R.drawable.tridesimtdu);
                        break;
                    case 33:
                        WeatherIcon.setImageResource(R.drawable.tridesimtrys);
                        break;
                    case 34:
                        WeatherIcon.setImageResource(R.drawable.trisdesimtketuri);
                        break;
                    case 35:
                        WeatherIcon.setImageResource(R.drawable.trisdesimtpenki);
                        break;
                    case 36:
                        WeatherIcon.setImageResource(R.drawable.trisdesimtsesi);
                        break;
                    case 37:
                        WeatherIcon.setImageResource(R.drawable.trisdesimtseptyni);
                        break;
                    case 38:
                        WeatherIcon.setImageResource(R.drawable.trisdesimtastuoni);
                        break;
                    case 39:
                        WeatherIcon.setImageResource(R.drawable.trisdesimtdevyni);
                        break;

                }






            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        }

    }

