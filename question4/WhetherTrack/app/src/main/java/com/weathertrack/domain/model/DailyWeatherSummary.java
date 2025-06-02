package com.weathertrack.domain.model;

public class DailyWeatherSummary {
    private double avgTemperature;
    private double avgHumidity;
    private String date;
    private double minTemperature;
    private double maxTemperature;
    private int recordCount;

    public DailyWeatherSummary() {}

    public DailyWeatherSummary(double avgTemperature, double avgHumidity, String date) {
        this.avgTemperature = avgTemperature;
        this.avgHumidity = avgHumidity;
        this.date = date;
    }

    // Getters and Setters
    public double getAvgTemperature() {
        return avgTemperature;
    }

    public void setAvgTemperature(double avgTemperature) {
        this.avgTemperature = avgTemperature;
    }

    public double getAvgHumidity() {
        return avgHumidity;
    }

    public void setAvgHumidity(double avgHumidity) {
        this.avgHumidity = avgHumidity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public String getFormattedAvgTemperature() {
        return String.format("%.1f°F", avgTemperature);
    }

    public String getTemperatureRange() {
        return String.format("%.1f°F - %.1f°F", minTemperature, maxTemperature);
    }
}
