package com.weathertrack.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.weathertrack.data.local.entity.WeatherEntity;

import java.util.List;

@Dao
public interface WeatherDao {
    
    @Insert
    void insertWeather(WeatherEntity weather);
    
    @Query("SELECT * FROM weather_records ORDER BY timestamp DESC LIMIT 1")
    LiveData<WeatherEntity> getLatestWeather();
    
    @Query("SELECT * FROM weather_records WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    LiveData<List<WeatherEntity>> getWeatherSince(long startTime);
    
    @Query("SELECT * FROM weather_records WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    LiveData<List<WeatherEntity>> getWeatherBetween(long startTime, long endTime);
    
    @Query("SELECT * FROM weather_records ORDER BY timestamp DESC")
    LiveData<List<WeatherEntity>> getAllWeather();
    
    @Query("DELETE FROM weather_records WHERE timestamp < :cutoffTime")
    void deleteOldRecords(long cutoffTime);
    
    @Query("SELECT COUNT(*) FROM weather_records")
    int getRecordCount();
    
    // Get daily averages for the past week
    @Query("SELECT AVG(temperature) as avgTemp, " +
           "AVG(humidity) as avgHumidity, " +
           "DATE(timestamp/1000, 'unixepoch') as date " +
           "FROM weather_records " +
           "WHERE timestamp >= :weekAgo " +
           "GROUP BY DATE(timestamp/1000, 'unixepoch') " +
           "ORDER BY date ASC")
    LiveData<List<DailyWeatherSummary>> getDailyAveragesForWeek(long weekAgo);
    
    // Inner class for daily summary
    class DailyWeatherSummary {
        public double avgTemp;
        public double avgHumidity;
        public String date;
        
        public DailyWeatherSummary(double avgTemp, double avgHumidity, String date) {
            this.avgTemp = avgTemp;
            this.avgHumidity = avgHumidity;
            this.date = date;
        }
    }
}
