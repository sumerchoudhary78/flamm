package com.weathertrack;

import android.app.Application;
import android.util.Log;

public class WeatherTrackApplication extends Application {
    
    private static final String TAG = "WeatherTrackApp";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "WeatherTrack Application started");
    }
}
