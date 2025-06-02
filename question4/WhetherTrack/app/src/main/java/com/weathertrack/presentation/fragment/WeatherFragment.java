package com.weathertrack.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.weathertrack.R;
import com.weathertrack.data.local.entity.WeatherEntity;
import com.weathertrack.presentation.viewmodel.WeatherViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherFragment extends Fragment {
    
    private WeatherViewModel viewModel;
    
    // UI Components
    private MaterialTextView tvTemperature;
    private MaterialTextView tvHumidity;
    private MaterialTextView tvCondition;
    private MaterialTextView tvCity;
    private MaterialTextView tvLastUpdated;
    private MaterialTextView tvStatus;
    private TextInputEditText etCity;
    private MaterialButton btnRefresh;
    private MaterialButton btnSetCity;
    private MaterialButton btnManualSync;
    private MaterialButton btnViewStats;
    private MaterialButton btnDismissStatus;
    private CircularProgressIndicator progressIndicator;
    private MaterialCardView weatherCard;
    private MaterialCardView statusCard;
    private LinearLayout weatherBackground;
    private ImageView ivWeatherIcon;
    private ImageView ivStatusIcon;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabRefresh;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        initViewModel();
        setupClickListeners();
        observeData();
    }
    
    private void initViews(View view) {
        tvTemperature = view.findViewById(R.id.tv_temperature);
        tvHumidity = view.findViewById(R.id.tv_humidity);
        tvCondition = view.findViewById(R.id.tv_condition);
        tvCity = view.findViewById(R.id.tv_city);
        tvLastUpdated = view.findViewById(R.id.tv_last_updated);
        tvStatus = view.findViewById(R.id.tv_status);
        etCity = view.findViewById(R.id.et_city);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        btnSetCity = view.findViewById(R.id.btn_set_city);
        btnManualSync = view.findViewById(R.id.btn_manual_sync);
        btnViewStats = view.findViewById(R.id.btn_view_stats);
        btnDismissStatus = view.findViewById(R.id.btn_dismiss_status);
        progressIndicator = view.findViewById(R.id.progress_indicator);
        weatherCard = view.findViewById(R.id.weather_card);
        statusCard = view.findViewById(R.id.status_card);
        weatherBackground = view.findViewById(R.id.weather_background);
        ivWeatherIcon = view.findViewById(R.id.iv_weather_icon);
        ivStatusIcon = view.findViewById(R.id.iv_status_icon);
        fabRefresh = view.findViewById(R.id.fab_refresh);
    }
    
    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
    }
    
    private void setupClickListeners() {
        btnRefresh.setOnClickListener(v -> {
            viewModel.clearError();
            viewModel.refreshWeather();
        });

        fabRefresh.setOnClickListener(v -> {
            viewModel.clearError();
            viewModel.refreshWeather();
        });

        btnSetCity.setOnClickListener(v -> {
            String city = etCity.getText() != null ? etCity.getText().toString().trim() : "";
            if (!city.isEmpty()) {
                viewModel.setCity(city);
                Toast.makeText(getContext(), "City set to: " + city, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please enter a city name", Toast.LENGTH_SHORT).show();
            }
        });

        btnManualSync.setOnClickListener(v -> {
            viewModel.performManualSync();
        });

        btnViewStats.setOnClickListener(v -> {
            // Navigate to weekly stats fragment
            if (getActivity() != null) {
                // This would typically use Navigation Component
                Toast.makeText(getContext(), "Navigating to Weekly Stats...", Toast.LENGTH_SHORT).show();
            }
        });

        btnDismissStatus.setOnClickListener(v -> {
            statusCard.setVisibility(View.GONE);
            viewModel.clearStatusMessage();
        });
    }
    
    private void observeData() {
        // Observe latest weather data
        viewModel.getLatestWeather().observe(getViewLifecycleOwner(), this::updateWeatherDisplay);
        
        // Observe loading state
        viewModel.getLoadingState().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                showStatusCard("Fetching weather data...", true, false);
            }
            btnRefresh.setEnabled(!isLoading);
            fabRefresh.setEnabled(!isLoading);
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showStatusCard(error, false, true);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        // Observe status messages
        viewModel.getStatusMessage().observe(getViewLifecycleOwner(), status -> {
            if (status != null && !status.isEmpty()) {
                showStatusCard(status, false, false);
            }
        });
        
        // Observe current city
        viewModel.getCurrentCity().observe(getViewLifecycleOwner(), city -> {
            if (etCity.getText() == null || etCity.getText().toString().isEmpty()) {
                etCity.setText(city);
            }
        });
    }
    
    private void updateWeatherDisplay(WeatherEntity weather) {
        if (weather != null) {
            weatherCard.setVisibility(View.VISIBLE);

            tvTemperature.setText(String.format(Locale.getDefault(), "%.1f°F", weather.getTemperature()));
            tvHumidity.setText(String.format(Locale.getDefault(), "%d%%", weather.getHumidity()));
            tvCondition.setText(weather.getCondition());
            tvCity.setText(weather.getCity());

            // Update weather background and icon based on condition
            updateWeatherTheme(weather.getCondition());

            // Format timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(new Date(weather.getTimestamp()));
            tvLastUpdated.setText(formattedDate);

            // Hide status card when weather is successfully loaded
            statusCard.setVisibility(View.GONE);
        } else {
            weatherCard.setVisibility(View.GONE);
        }
    }

    private void updateWeatherTheme(String condition) {
        int backgroundRes;
        int iconRes;

        if (condition.toLowerCase().contains("sunny") || condition.toLowerCase().contains("clear")) {
            backgroundRes = R.drawable.gradient_sunny;
            iconRes = R.drawable.ic_sunny;
        } else if (condition.toLowerCase().contains("rain")) {
            backgroundRes = R.drawable.gradient_rainy;
            iconRes = R.drawable.ic_rainy;
        } else {
            backgroundRes = R.drawable.gradient_cloudy;
            iconRes = R.drawable.ic_cloudy;
        }

        weatherBackground.setBackgroundResource(backgroundRes);
        ivWeatherIcon.setImageResource(iconRes);
    }

    private void showStatusCard(String message, boolean showProgress, boolean isError) {
        statusCard.setVisibility(View.VISIBLE);
        tvStatus.setText(message);

        if (showProgress) {
            progressIndicator.setVisibility(View.VISIBLE);
            ivStatusIcon.setVisibility(View.GONE);
        } else {
            progressIndicator.setVisibility(View.GONE);
            ivStatusIcon.setVisibility(View.VISIBLE);

            if (isError) {
                ivStatusIcon.setImageResource(R.drawable.ic_error);
                ivStatusIcon.setColorFilter(getResources().getColor(R.color.error_red, null));
            } else {
                ivStatusIcon.setImageResource(R.drawable.ic_info);
                ivStatusIcon.setColorFilter(getResources().getColor(R.color.success_green, null));
            }
        }
    }
}
