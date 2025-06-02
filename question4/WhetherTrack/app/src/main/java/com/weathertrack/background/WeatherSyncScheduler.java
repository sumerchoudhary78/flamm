package com.weathertrack.background;

import android.content.Context;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class WeatherSyncScheduler {
    
    private static final String TAG = "WeatherSyncScheduler";
    private static final String WORK_NAME = "weather_sync_work";
    private static final long SYNC_INTERVAL_HOURS = 6;
    
    public static void scheduleWeatherSync(Context context, String city) {
        Log.d(TAG, "Scheduling weather sync every " + SYNC_INTERVAL_HOURS + " hours");
        
        // Create constraints - only run when connected to network
        Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();
        
        // Create input data
        Data inputData = new Data.Builder()
            .putString("city", city)
            .build();
        
        // Create periodic work request
        PeriodicWorkRequest weatherSyncWork = new PeriodicWorkRequest.Builder(
                WeatherSyncWorker.class,
                SYNC_INTERVAL_HOURS,
                TimeUnit.HOURS
            )
            .setConstraints(constraints)
            .setInputData(inputData)
            .build();
        
        // Enqueue the work, replacing any existing work with the same name
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                weatherSyncWork
            );
        
        Log.d(TAG, "Weather sync scheduled successfully");
    }
    
    public static void cancelWeatherSync(Context context) {
        Log.d(TAG, "Cancelling weather sync");
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME);
    }
    
    public static void scheduleOneTimeSync(Context context, String city) {
        Log.d(TAG, "Scheduling one-time weather sync");
        
        Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();
        
        Data inputData = new Data.Builder()
            .putString("city", city)
            .build();
        
        androidx.work.OneTimeWorkRequest oneTimeWork = 
            new androidx.work.OneTimeWorkRequest.Builder(WeatherSyncWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        
        WorkManager.getInstance(context).enqueue(oneTimeWork);
    }
}
