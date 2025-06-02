# 🌤️ WeatherTrack - Android Weather Tracking App

A comprehensive Android application for tracking daily weather statistics with automatic background sync, local storage, and weekly trend analysis.

## 📋 Question 4 Requirements Compliance

### ✅ **Feature Requirements Implementation**

| Requirement | Status | Implementation |
|-------------|--------|----------------|
| **1. Fetch Weather** | ✅ Complete | Mock API + Room database with timestamps |
| **2. Auto Background Sync** | ✅ Complete | WorkManager every 6 hours + manual refresh |
| **3. Weekly Summary Screen** | ✅ Complete | Chart + list view with 7-day trends |
| **4. App Architecture** | ✅ Complete | Java + MVVM with proper layer separation |
| **5. Error Handling** | ✅ Complete | Network, API, and database error handling |

## 🏗️ **Architecture Overview**

### **MVVM Architecture Pattern**
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   UI Layer      │    │  ViewModel      │    │  Repository     │
│                 │    │                 │    │                 │
│ • MainActivity  │◄──►│ WeatherViewModel│◄──►│WeatherRepository│
│ • WeeklySummary │    │                 │    │                 │
│ • WeatherDetail │    │ • LiveData      │    │ • Data Sources  │
└─────────────────┘    │ • Error Handling│    │ • API Service   │
                       └─────────────────┘    │ • Room Database │
                                              └─────────────────┘
```

### **Layer Separation**
- **UI Layer**: Activities, Fragments, Adapters
- **ViewModel Layer**: Business logic, LiveData management
- **Repository Layer**: Data source coordination
- **Data Layer**: Room database, API service, WorkManager

## 🚀 **Key Features**

### **1. Weather Fetching**
- **Mock API Service**: Realistic weather data simulation
- **Automatic Timestamps**: Every fetch includes precise timestamp
- **Room Database Storage**: Local persistence with SQLite
- **Error Simulation**: 10% API failure rate for testing

### **2. Background Sync**
- **WorkManager Integration**: Reliable background execution
- **6-Hour Intervals**: Automatic weather updates
- **Network Constraints**: Only runs with internet connection
- **Battery Optimization**: Respects battery saver mode
- **Manual Refresh**: Immediate update capability

### **3. Weekly Summary**
- **Interactive Chart**: MPAndroidChart line graph
- **7-Day Trends**: Temperature visualization
- **Daily Aggregation**: Average, min, max temperatures
- **Clickable Items**: Detailed view for each day
- **Statistics Display**: Comprehensive weekly stats

### **4. Error Handling**
- **Network Errors**: "No internet connection" messages
- **API Errors**: "Service unavailable" notifications
- **Database Errors**: Local storage issue handling
- **User-Friendly Messages**: Clear, actionable error text

## 📱 **User Interface**

### **Main Screen**
- Current weather display with temperature, humidity, condition
- City input with validation
- Refresh button with loading states
- Error/success message cards
- Weather icons based on conditions

### **Weekly Summary Screen**
- Interactive temperature chart (last 7 days)
- Daily weather summary list
- Statistics: average, min, max temperatures
- Click-to-detail functionality

### **Weather Detail Screen**
- Comprehensive weather information
- Historical data view
- Timestamp and location details

## 🛠️ **Technical Implementation**

### **Database Schema**
```sql
CREATE TABLE weather_data (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    temperature REAL NOT NULL,
    humidity INTEGER NOT NULL,
    condition TEXT NOT NULL,
    city TEXT NOT NULL,
    timestamp INTEGER NOT NULL,
    wind_speed REAL,
    pressure INTEGER,
    description TEXT
);
```

### **API Service**
```java
public interface WeatherApiService {
    Single<WeatherResponse> getCurrentWeather(@Query("city") String city);
    Single<WeatherResponse> getMockWeather(@Query("city") String city);
}
```

### **WorkManager Configuration**
```java
PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
    WeatherSyncWorker.class,
    6, TimeUnit.HOURS,  // Repeat every 6 hours
    15, TimeUnit.MINUTES // Flex interval
)
.setConstraints(constraints)
.build();
```

## 📦 **Dependencies**

### **Core Android**
- `androidx.appcompat:appcompat:1.6.1`
- `androidx.lifecycle:lifecycle-viewmodel:2.7.0`
- `androidx.activity:activity:1.8.0`

### **Database**
- `androidx.room:room-runtime:2.6.0`
- `androidx.room:room-rxjava3:2.6.0`

### **Background Work**
- `androidx.work:work-runtime:2.9.0`

### **Networking**
- `com.squareup.retrofit2:retrofit:2.9.0`
- `com.squareup.retrofit2:converter-gson:2.9.0`

### **Charts**
- `com.github.PhilJay:MPAndroidChart:v3.1.0`

### **Reactive Programming**
- `io.reactivex.rxjava3:rxjava:3.1.8`
- `io.reactivex.rxjava3:rxandroid:3.0.2`

## 🔧 **Setup Instructions**

### **1. Clone Repository**
```bash
git clone <repository-url>
cd WeatherTrack
```

### **2. Open in Android Studio**
- Open Android Studio
- Select "Open an existing project"
- Navigate to the project directory
- Wait for Gradle sync to complete

### **3. Build and Run**
```bash
./gradlew assembleDebug
./gradlew installDebug
```

### **4. Testing**
```bash
./gradlew test
./gradlew connectedAndroidTest
```

## 📊 **Data Flow**

### **Weather Fetch Flow**
1. User clicks refresh or WorkManager triggers
2. Repository checks network availability
3. API service fetches mock weather data
4. Data converted to Room entity
5. Saved to local database with timestamp
6. UI updated via LiveData observation

### **Weekly Summary Flow**
1. Repository queries last 7 days of data
2. Data grouped by day for aggregation
3. Chart data calculated (daily averages)
4. RecyclerView updated with daily summaries
5. Statistics computed and displayed

## 🧪 **Testing Strategy**

### **Unit Tests**
- Repository layer testing
- ViewModel business logic
- Data transformation methods
- Error handling scenarios

### **Integration Tests**
- Database operations
- API service responses
- WorkManager execution
- End-to-end data flow

### **UI Tests**
- User interaction flows
- Error message display
- Chart rendering
- Navigation between screens

## 🔒 **Error Handling Examples**

### **Network Error**
```java
if (!isNetworkAvailable()) {
    return Single.error(new NetworkException("No internet connection available"));
}
```

### **API Error**
```java
.onErrorResumeNext(throwable -> {
    if (throwable.getMessage().contains("API Error")) {
        return Single.error(new ApiException("Weather service is currently unavailable"));
    }
    return Single.error(throwable);
});
```

### **Database Error**
```java
.onErrorResumeNext(throwable -> 
    Single.error(new DatabaseException("Failed to save weather data to local storage")));
```

## 📈 **Performance Optimizations**

- **Lazy Loading**: Data loaded on demand
- **Background Threading**: All database/network operations off main thread
- **Memory Management**: Proper disposal of RxJava subscriptions
- **Battery Optimization**: WorkManager respects system constraints
- **Efficient Queries**: Optimized Room database queries

## 🎯 **Future Enhancements**

- Weather notifications
- Multiple city support
- Weather alerts and warnings
- Data export functionality
- Dark mode support
- Widget implementation

## 📄 **License**

This project is created for educational purposes as part of Android Assignment Set 2, Question 4.

---

**WeatherTrack** - Your reliable companion for weather tracking! 🌤️
