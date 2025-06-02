package com.weathertrack.background;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.weathertrack.data.repository.WeatherRepository;

public class WeatherSyncWorker extends Worker {
    
    private static final String TAG = "WeatherSyncWorker";
    private static final String DEFAULT_CITY = "New York";
    
    public WeatherSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Starting weather sync work");
        
        try {
            WeatherRepository repository = new WeatherRepository(getApplicationContext());
            String city = getInputData().getString("city");
            if (city == null || city.isEmpty()) {
                city = DEFAULT_CITY;
            }
            
            // Fetch weather synchronously for background work
            repository.fetchAndSaveWeather(city)
                .get(); // This blocks until completion
            
            Log.d(TAG, "Weather sync completed successfully");
            return Result.success();
            
        } catch (Exception e) {
            Log.e(TAG, "Weather sync failed", e);
            
            // Retry on failure, but limit retries
            if (getRunAttemptCount() < 3) {
                Log.d(TAG, "Retrying weather sync, attempt: " + (getRunAttemptCount() + 1));
                return Result.retry();
            } else {
                Log.e(TAG, "Weather sync failed after 3 attempts, giving up");
                return Result.failure();
            }
        }
    }
}
