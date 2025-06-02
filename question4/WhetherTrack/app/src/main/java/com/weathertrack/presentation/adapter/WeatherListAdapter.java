package com.weathertrack.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.weathertrack.R;
import com.weathertrack.data.local.entity.WeatherEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherListAdapter extends RecyclerView.Adapter<WeatherListAdapter.WeatherViewHolder> {
    
    private List<WeatherEntity> weatherList = new ArrayList<>();
    private OnWeatherItemClickListener clickListener;
    
    public interface OnWeatherItemClickListener {
        void onWeatherItemClick(WeatherEntity weather);
    }
    
    public void setWeatherList(List<WeatherEntity> weatherList) {
        this.weatherList = weatherList != null ? weatherList : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void setOnWeatherItemClickListener(OnWeatherItemClickListener listener) {
        this.clickListener = listener;
    }
    
    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_weather, parent, false);
        return new WeatherViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherEntity weather = weatherList.get(position);
        holder.bind(weather);
        
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onWeatherItemClick(weather);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return weatherList.size();
    }
    
    static class WeatherViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView tvTemperature;
        private final MaterialTextView tvHumidity;
        private final MaterialTextView tvCondition;
        private final MaterialTextView tvDateTime;
        private final MaterialTextView tvCity;
        private final android.widget.ImageView ivWeatherIcon;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTemperature = itemView.findViewById(R.id.tv_temperature);
            tvHumidity = itemView.findViewById(R.id.tv_humidity);
            tvCondition = itemView.findViewById(R.id.tv_condition);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvCity = itemView.findViewById(R.id.tv_city);
            ivWeatherIcon = itemView.findViewById(R.id.iv_weather_icon);
        }
        
        public void bind(WeatherEntity weather) {
            tvTemperature.setText(String.format(Locale.getDefault(), "%.1f°F", weather.getTemperature()));
            tvHumidity.setText(String.format(Locale.getDefault(), "%d%%", weather.getHumidity()));
            tvCondition.setText(weather.getCondition());
            tvCity.setText(weather.getCity());

            // Set weather icon based on condition
            updateWeatherIcon(weather.getCondition());

            // Format date and time
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
            String formattedDateTime = sdf.format(new Date(weather.getTimestamp()));
            tvDateTime.setText(formattedDateTime);
        }

        private void updateWeatherIcon(String condition) {
            int iconRes;

            if (condition.toLowerCase().contains("sunny") || condition.toLowerCase().contains("clear")) {
                iconRes = com.weathertrack.R.drawable.ic_sunny;
            } else if (condition.toLowerCase().contains("rain")) {
                iconRes = com.weathertrack.R.drawable.ic_rainy;
            } else {
                iconRes = com.weathertrack.R.drawable.ic_cloudy;
            }

            ivWeatherIcon.setImageResource(iconRes);
        }
    }
}
