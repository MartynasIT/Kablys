package com.example.kablys;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class WeatherFragment extends Fragment {

    private ArrayList<WeatherObject> weatherArrayList = new ArrayList<>();
    private ListView listView;
    private ArrayList<String[]> Calendar = new ArrayList<String[]>();
    private TextView temp, pressure, humidity, fishBite, toFish;
    private ImageView WeatherIcon;
    DatabaseAPI db;
    private String pressureTendency;
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
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        listView = view.findViewById(R.id.weatherList);
       temp = view.findViewById(R.id.TempNow);
       WeatherIcon = view.findViewById(R.id.conditionToday);
       pressure = view.findViewById(R.id.PressureNow);
        humidity = view.findViewById(R.id.HumidityNow);
       fishBite = view.findViewById(R.id.biteNow);
       view.findViewById(R.id.biteNow).setSelected(true); // kad veiktu scrolinimo animacija
        WeatherNow = view.findViewById(R.id.weatherNow);
        toFish = view.findViewById(R.id.FishesToFIsh);
        view.findViewById(R.id.FishesToFIsh).setSelected(true);
        db = new DatabaseAPI(ctx);
        Calendar = db.getCalendar();

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
                weatherSearchResults = JsonGettter.getJsonData(weatherUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return weatherSearchResults;
        }

        @Override
        protected void onPostExecute(String weatherSearchResults) {

            WeatherAdapter weatherAdapter = new WeatherAdapter(ctx, weatherArrayList);

            if(weatherSearchResults != null && !weatherSearchResults.equals("") ) {
                weatherArrayList = JsonFIveDays(weatherSearchResults, weatherAdapter);
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
            URL APIurl = urls[0];
            String weatherResults = null;

            try {
                weatherResults = JsonGettter.getJsonData(APIurl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return weatherResults;
        }

        @Override
        protected void onPostExecute(String weatherResults) {
            if(weatherResults != null && !weatherResults.equals("") ) {

            JsonNow(weatherResults);
            }

            super.onPostExecute(weatherResults);
        }
    }


    private ArrayList<WeatherObject> JsonFIveDays(String weatherResults,  WeatherAdapter weatherAdapter) {

        if(weatherArrayList != null) {
            weatherArrayList.clear();
        }

        if(weatherResults != null) {
            try {
                JSONObject rootObject = new JSONObject(weatherResults);
                JSONArray results = rootObject.getJSONArray("DailyForecasts");

                for (int i = 0; i < results.length(); i++) {
                    WeatherObject weatherObject = new WeatherObject();

                    JSONObject resultsObj = results.getJSONObject(i);

                    String date = resultsObj.getString("Date");
                    date = date.substring(0,10);
                    weatherObject.setDate(date);

                    JSONObject temperatureObj = resultsObj.getJSONObject("Temperature");
                    String minTemperature = temperatureObj.getJSONObject("Minimum").getString("Value");
                    weatherObject.setMinTemp(minTemperature +" C");

                    String maxTemperature = temperatureObj.getJSONObject("Maximum").getString("Value");
                    weatherObject.setMaxTemp(maxTemperature + " C");

                    JSONObject dayObj = resultsObj.getJSONObject("Day");
                    int condition = dayObj.getInt("Icon");
                    weatherObject.setCondition(condition);


                    weatherArrayList.add(weatherObject);
                    weatherAdapter.notifyDataSetChanged();
                }

                if(weatherArrayList != null) {

                    listView.setAdapter(weatherAdapter);
                }

                return weatherArrayList;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private void JsonNow(String weatherResults) {

        if(weatherResults != null) {
            try {
                JSONArray mJsonArray = new JSONArray(weatherResults);
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
                int d = 0;
                if (pressure >= 30.50 && pressureTendency.equals("Steady"))
                {
                    bite = "Prognozė: Vidutinis arba lėtas kibimas, dėl aukšto slėgio. Žvejok kantriai giliame vandenyje";
                    fishBite.setText(bite);
                    d = 0;
                    fishBite.setTextColor(Color.YELLOW);
                }

                else if (pressureTendency.equals("Rising") && pressure < 30.50)
                {
                    bite = "Prognozė: Žuvys atkyvėja dėl augančio slėgio, bet aktyvumas dugną mėgstančių žuvų mažėja";
                    fishBite.setText(bite);
                    d =1;
                    fishBite.setTextColor(Color.GREEN);
                }

                else if (pressureTendency.equals("Falling") && pressure <= 30.50  &&  pressure > 29.60 )
                {
                    bite = "Prognozė: Žuvų aktyvumas mažėja, dėl krentančio slėgio. Bet yra išimčių kai kurioms plėšrių žuvų rūšims, kaip šamai, lydekos";
                    fishBite.setText(bite);
                    d = 0;
                    fishBite.setTextColor(Color.YELLOW);
                }

               else if (pressure >= 29.70 && pressure <= 30.40 && pressureTendency.equals("Steady"))
                {
                    bite = "Prognozė: Normalus kibimas, pabandytk įvairius jaukus ir technikas";
                    fishBite.setText(bite);
                    d = 1;
                    fishBite.setTextColor(Color.GREEN);
                }

                else if (pressure <= 29.60 && pressureTendency.equals("Steady"))
                {
                    bite = "Prognozė: Lėtas kibimas, dėl žemo slėgio, reiktu žvejoti giliai ir lėtai";
                    fishBite.setText(bite);
                    d = 0;
                    fishBite.setTextColor(Color.RED);
                }

                else if (pressure <= 29.60 && pressureTendency.equals("Falling"))
                {
                    bite = "Prognozė: Lėtas kibimas, dėl žemo slėgio, reiktu žvejoti giliai ir lėtai";
                    fishBite.setText(bite);
                    d = 0;
                    fishBite.setTextColor(Color.RED);
                }


                else
                {
                    bite = "Neaiški prgonozė, paminėtos žuvys gali ir nekibti";
                    d = 1;
                    fishBite.setText(bite);

                }


                DateFormat dateFormat = new SimpleDateFormat("MM");
                Date date = new Date();
               String monthNR = dateFormat.format(date);
               String month = monthToStr(monthNR);
               String fishes = "";

               if (d!=0) {
                   ArrayList alreadyPut = new ArrayList();
                   Iterator<String[]> itr = Calendar.iterator();
                   while (itr.hasNext()) {
                       String[] array = itr.next();
                       if (array[1].equals(month) && array[2].equals("geras") || array[2].equals("vidutinis") && !alreadyPut.contains(array[0]))
                           fishes += array[0] +  ", ";
                           alreadyPut.add(array[0]);
                   }

                   if (!fishes.equals("")) {
                       toFish.setText("Gerai kibs: " + fishes);
                       alreadyPut.clear();
                       toFish.setTextColor(Color.GREEN);
                   }

               }

                else  {
                    toFish.setText("Gerai kibs: nelabai kas");
                    toFish.setTextColor(Color.RED);
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

        private String monthToStr(String month)
        {
            switch (month) {
                case "1":
                    month = "sausis";
                    break;
                case "2":
                    month = "vasaris";
                    break;
                case "3":
                    month = "kovas";
                    break;
                case "4":
                    month = "balandis";
                    break;
                case "5":
                    month = "geguže";
                    break;
                case "6":
                    month = "birželis";
                    break;
                case "7":
                    month = "liepa";
                    break;
                case "8":
                    month = "rugpjūtis";
                    break;
                case "9":
                    month = "rugsėjis";
                    break;
                case "10":
                    month = "spalis";
                    break;
                case "11":
                    month = "lapkritis";
                    break;
                case "12":
                    month = "gruodis";
                    break;

            }
            return month;
        }

    }

