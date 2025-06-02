package com.weathertrack.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.weathertrack.data.local.dao.WeatherDao;
import com.weathertrack.data.local.database.WeatherDatabase;
import com.weathertrack.data.local.entity.WeatherEntity;
import com.weathertrack.data.remote.MockWeatherApi;
import com.weathertrack.domain.model.WeatherModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherRepository {
    
    private final WeatherDao weatherDao;
    private final MockWeatherApi mockApi;
    private final ExecutorService executor;
    private final MutableLiveData<String> errorLiveData;
    private final MutableLiveData<Boolean> loadingLiveData;

    public WeatherRepository(Context context) {
        WeatherDatabase database = WeatherDatabase.getDatabase(context);
        this.weatherDao = database.weatherDao();
        this.mockApi = new MockWeatherApi();
        this.executor = Executors.newFixedThreadPool(4);
        this.errorLiveData = new MutableLiveData<>();
        this.loadingLiveData = new MutableLiveData<>(false);
    }

    public CompletableFuture<WeatherModel> fetchAndSaveWeather(String city) {
        loadingLiveData.postValue(true);
        
        return mockApi.getCurrentWeather(city)
            .thenApply(weather -> {
                // Save to database
                WeatherEntity entity = new WeatherEntity(
                    weather.getTemperature(),
                    weather.getHumidity(),
                    weather.getCondition(),
                    weather.getTimestamp(),
                    weather.getCity()
                );
                
                executor.execute(() -> weatherDao.insertWeather(entity));
                
                loadingLiveData.postValue(false);
                errorLiveData.postValue(null); // Clear any previous errors
                return weather;
            })
            .exceptionally(throwable -> {
                loadingLiveData.postValue(false);
                String errorMessage = "Failed to fetch weather: " + throwable.getMessage();
                errorLiveData.postValue(errorMessage);
                return null;
            });
    }

    public LiveData<WeatherEntity> getLatestWeather() {
        return weatherDao.getLatestWeather();
    }

    public LiveData<List<WeatherEntity>> getWeatherSince(long timestamp) {
        return weatherDao.getWeatherSince(timestamp);
    }

    public LiveData<List<WeatherEntity>> getWeatherBetween(long startTime, long endTime) {
        return weatherDao.getWeatherBetween(startTime, endTime);
    }

    public LiveData<List<WeatherEntity>> getAllWeather() {
        return weatherDao.getAllWeather();
    }

    public LiveData<List<WeatherDao.DailyWeatherSummary>> getDailyAveragesForWeek(long weekAgo) {
        return weatherDao.getDailyAveragesForWeek(weekAgo);
    }

    public void cleanupOldRecords(long cutoffTime) {
        executor.execute(() -> weatherDao.deleteOldRecords(cutoffTime));
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    public void clearError() {
        errorLiveData.postValue(null);
    }
}
