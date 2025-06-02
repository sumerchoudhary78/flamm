package com.weathertrack.presentation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.weathertrack.background.WeatherSyncScheduler;
import com.weathertrack.data.local.entity.WeatherEntity;
import com.weathertrack.data.repository.WeatherRepository;
import com.weathertrack.domain.model.WeatherModel;

import java.util.List;

public class WeatherViewModel extends AndroidViewModel {
    
    private final WeatherRepository repository;
    private final MutableLiveData<String> currentCity;
    private final MutableLiveData<String> statusMessage;
    
    public WeatherViewModel(@NonNull Application application) {
        super(application);
        this.repository = new WeatherRepository(application);
        this.currentCity = new MutableLiveData<>("New York");
        this.statusMessage = new MutableLiveData<>();
        
        // Schedule background sync when ViewModel is created
        scheduleBackgroundSync();
    }

    public void refreshWeather() {
        String city = currentCity.getValue();
        if (city == null || city.isEmpty()) {
            city = "New York";
        }
        
        statusMessage.setValue("Fetching weather data...");
        
        repository.fetchAndSaveWeather(city)
            .thenAccept(weather -> {
                if (weather != null) {
                    statusMessage.postValue("Weather updated successfully");
                } else {
                    statusMessage.postValue("Failed to fetch weather data");
                }
            })
            .exceptionally(throwable -> {
                statusMessage.postValue("Error: " + throwable.getMessage());
                return null;
            });
    }

    public void setCity(String city) {
        currentCity.setValue(city);
        // Reschedule background sync with new city
        scheduleBackgroundSync();
    }

    public void scheduleBackgroundSync() {
        String city = currentCity.getValue();
        if (city != null && !city.isEmpty()) {
            WeatherSyncScheduler.scheduleWeatherSync(getApplication(), city);
        }
    }

    public void performManualSync() {
        String city = currentCity.getValue();
        if (city != null && !city.isEmpty()) {
            WeatherSyncScheduler.scheduleOneTimeSync(getApplication(), city);
            statusMessage.setValue("Manual sync initiated...");
        }
    }

    // LiveData getters
    public LiveData<WeatherEntity> getLatestWeather() {
        return repository.getLatestWeather();
    }

    public LiveData<List<WeatherEntity>> getAllWeather() {
        return repository.getAllWeather();
    }

    public LiveData<String> getCurrentCity() {
        return currentCity;
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    public LiveData<String> getErrorMessage() {
        return repository.getErrorLiveData();
    }

    public LiveData<Boolean> getLoadingState() {
        return repository.getLoadingLiveData();
    }

    public void clearError() {
        repository.clearError();
        statusMessage.setValue(null);
    }

    public void clearStatusMessage() {
        statusMessage.setValue(null);
    }
}
