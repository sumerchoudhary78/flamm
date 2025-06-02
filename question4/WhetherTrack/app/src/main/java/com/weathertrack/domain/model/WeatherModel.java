package com.weathertrack.domain.model;

public class WeatherModel {
    private double temperature;
    private int humidity;
    private String condition;
    private long timestamp;
    private String city;

    public WeatherModel() {}

    public WeatherModel(double temperature, int humidity, String condition, long timestamp, String city) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.condition = condition;
        this.timestamp = timestamp;
        this.city = city;
    }

    // Getters and Setters
    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFormattedTemperature() {
        return String.format("%.1f°F", temperature);
    }

    public String getFormattedHumidity() {
        return humidity + "%";
    }
}
