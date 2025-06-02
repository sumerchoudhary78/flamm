package com.weathertrack.presentation.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.weathertrack.R;
import com.weathertrack.data.local.entity.WeatherEntity;
import com.weathertrack.presentation.adapter.WeatherListAdapter;
import com.weathertrack.presentation.viewmodel.WeeklyStatsViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WeeklyStatsFragment extends Fragment {
    
    private WeeklyStatsViewModel viewModel;
    
    // UI Components
    private LineChart temperatureChart;
    private RecyclerView recyclerView;
    private WeatherListAdapter adapter;
    private MaterialTextView tvNoData;
    private MaterialButton btnRefresh;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weekly_stats, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        initViewModel();
        setupRecyclerView();
        setupChart();
        setupClickListeners();
        observeData();
    }
    
    private void initViews(View view) {
        temperatureChart = view.findViewById(R.id.temperature_chart);
        recyclerView = view.findViewById(R.id.recycler_view);
        tvNoData = view.findViewById(R.id.tv_no_data);
        btnRefresh = view.findViewById(R.id.btn_refresh);
    }
    
    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(WeeklyStatsViewModel.class);
    }
    
    private void setupRecyclerView() {
        adapter = new WeatherListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    
    private void setupChart() {
        temperatureChart.setTouchEnabled(true);
        temperatureChart.setDragEnabled(true);
        temperatureChart.setScaleEnabled(true);
        temperatureChart.setPinchZoom(true);
        
        Description description = new Description();
        description.setText("Temperature Trends (Past 7 Days)");
        temperatureChart.setDescription(description);
        
        XAxis xAxis = temperatureChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
    }
    
    private void setupClickListeners() {
        btnRefresh.setOnClickListener(v -> viewModel.refreshWeeklyData());
    }
    
    private void observeData() {
        viewModel.getWeeklyWeatherData().observe(getViewLifecycleOwner(), weatherList -> {
            if (weatherList != null && !weatherList.isEmpty()) {
                tvNoData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                temperatureChart.setVisibility(View.VISIBLE);
                
                adapter.setWeatherList(weatherList);
                updateChart(weatherList);
            } else {
                tvNoData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                temperatureChart.setVisibility(View.GONE);
            }
        });
    }
    
    private void updateChart(List<WeatherEntity> weatherList) {
        if (weatherList == null || weatherList.isEmpty()) {
            return;
        }
        
        // Group data by day and calculate daily averages
        Map<String, List<Double>> dailyTemperatures = new HashMap<>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        
        for (WeatherEntity weather : weatherList) {
            String day = dayFormat.format(new Date(weather.getTimestamp()));
            dailyTemperatures.computeIfAbsent(day, k -> new ArrayList<>()).add(weather.getTemperature());
        }
        
        // Create chart entries
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;
        
        // Sort by date (last 7 days)
        Calendar calendar = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            String day = dayFormat.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, i); // Reset
            
            if (dailyTemperatures.containsKey(day)) {
                List<Double> temps = dailyTemperatures.get(day);
                double avgTemp = temps.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                entries.add(new Entry(index, (float) avgTemp));
            }
            labels.add(day);
            index++;
        }
        
        if (!entries.isEmpty()) {
            LineDataSet dataSet = new LineDataSet(entries, "Average Temperature (°F)");
            dataSet.setColor(Color.BLUE);
            dataSet.setCircleColor(Color.BLUE);
            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(4f);
            dataSet.setValueTextSize(10f);
            
            LineData lineData = new LineData(dataSet);
            temperatureChart.setData(lineData);
            
            XAxis xAxis = temperatureChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setLabelCount(labels.size());
            
            temperatureChart.invalidate(); // Refresh chart
        }
    }
}
