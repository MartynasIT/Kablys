package com.example.kablys;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Milind Amrutkar on 29-11-2017.
 */

public class NetworkUtils {

    public static URL buildUrlForWeather() {

        String url = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/231459?apikey=uKL3VAbABHq6mNUGJ91mOMmFwjt6JneS&metric=true";
        String UrlCurrent = "http://dataservice.accuweather.com/currentconditions/v1/?apikey=uKL3VAbABHq6mNUGJ91mOMmFwjt6JneS&details=true";

        URL wetherUrl = null;
        try {
            wetherUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return wetherUrl;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in  = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
