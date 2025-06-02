package com.weathertrack.presentation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.weathertrack.data.local.dao.WeatherDao;
import com.weathertrack.data.local.entity.WeatherEntity;
import com.weathertrack.data.repository.WeatherRepository;

import java.util.Calendar;
import java.util.List;

public class WeeklyStatsViewModel extends AndroidViewModel {
    
    private final WeatherRepository repository;
    private final MutableLiveData<Long> weekStartTime;
    
    public WeeklyStatsViewModel(@NonNull Application application) {
        super(application);
        this.repository = new WeatherRepository(application);
        this.weekStartTime = new MutableLiveData<>();
        
        // Set initial week start time (7 days ago)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        weekStartTime.setValue(calendar.getTimeInMillis());
    }

    public LiveData<List<WeatherEntity>> getWeeklyWeatherData() {
        return Transformations.switchMap(weekStartTime, startTime -> 
            repository.getWeatherSince(startTime)
        );
    }

    public LiveData<List<WeatherDao.DailyWeatherSummary>> getDailyAverages() {
        return Transformations.switchMap(weekStartTime, startTime ->
            repository.getDailyAveragesForWeek(startTime)
        );
    }

    public void setWeekStartTime(long startTime) {
        weekStartTime.setValue(startTime);
    }

    public void refreshWeeklyData() {
        // Trigger refresh by setting the same value
        Long currentStartTime = weekStartTime.getValue();
        if (currentStartTime != null) {
            weekStartTime.setValue(currentStartTime);
        }
    }

    public LiveData<Long> getWeekStartTime() {
        return weekStartTime;
    }
}
