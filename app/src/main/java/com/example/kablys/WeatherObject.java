package com.example.kablys;

public class WeatherObject {
    String date;
    String minTemp;
    String maxTemp;
    int condition;

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }


    public void setCondition(int condition)
    {
        this.condition = condition;
    }

    public int getCondition()
    {

        return condition;
    }


    public String getMinTemp()
    {

        return minTemp;
    }

    public void setMinTemp(String minTemp)
    {
        this.minTemp = minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp)
    {
        this.maxTemp = maxTemp;
    }
}

