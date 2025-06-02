package com.weathertrack.data.remote;

import com.weathertrack.domain.model.WeatherModel;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class MockWeatherApi {
    
    private static final String[] CONDITIONS = {
        "Sunny", "Cloudy", "Rainy", "Partly Cloudy", "Overcast", "Light Rain", "Heavy Rain"
    };
    
    private static final String DEFAULT_CITY = "New York";
    private final Random random = new Random();
    
    public CompletableFuture<WeatherModel> getCurrentWeather(String city) {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate network delay
            try {
                Thread.sleep(1000 + random.nextInt(2000)); // 1-3 seconds delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("API call interrupted", e);
            }
            
            // Simulate occasional API failures (10% chance)
            if (random.nextInt(10) == 0) {
                throw new RuntimeException("Mock API Error: Service temporarily unavailable");
            }
            
            // Generate realistic weather data
            double temperature = generateTemperature();
            int humidity = 30 + random.nextInt(70); // 30-100%
            String condition = CONDITIONS[random.nextInt(CONDITIONS.length)];
            
            return new WeatherModel(
                temperature,
                humidity,
                condition,
                System.currentTimeMillis(),
                city != null ? city : DEFAULT_CITY
            );
        });
    }
    
    private double generateTemperature() {
        // Generate temperature based on current season (simplified)
        int month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
        double baseTemp;
        
        if (month >= 11 || month <= 2) { // Winter
            baseTemp = 35 + random.nextInt(25); // 35-60°F
        } else if (month >= 3 && month <= 5) { // Spring
            baseTemp = 50 + random.nextInt(30); // 50-80°F
        } else if (month >= 6 && month <= 8) { // Summer
            baseTemp = 70 + random.nextInt(25); // 70-95°F
        } else { // Fall
            baseTemp = 45 + random.nextInt(35); // 45-80°F
        }
        
        // Add some random variation
        return baseTemp + (random.nextGaussian() * 5);
    }
}
