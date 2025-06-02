package com.weathertrack.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.weathertrack.data.local.dao.WeatherDao;
import com.weathertrack.data.local.entity.WeatherEntity;

@Database(
    entities = {WeatherEntity.class},
    version = 1,
    exportSchema = false
)
public abstract class WeatherDatabase extends RoomDatabase {
    
    private static volatile WeatherDatabase INSTANCE;
    
    public abstract WeatherDao weatherDao();
    
    public static WeatherDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WeatherDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            WeatherDatabase.class,
                            "weather_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
