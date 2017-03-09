package com.amsterly.lovecoder.lovecoder.ui.adapter;


import com.amsterly.lovecoder.lovecoder.R;
import com.amsterly.lovecoder.lovecoder.model.entity.WeatherEntity;

/**
 * Created by SilenceDut on 16/10/20.
 */

public class DailyWeatherData implements BaseAdapterData {

    public WeatherEntity.DailyForecastEntity dailyForecastData;

    public DailyWeatherData(WeatherEntity.DailyForecastEntity dailyForecastData) {
        this.dailyForecastData = dailyForecastData;
    }


    @Override
    public int getItemViewType() {
        return R.layout.item_daily_forecast;
    }
}
