package com.amsterly.lovecoder.lovecoder.model;


import com.amsterly.lovecoder.lovecoder.R;
import com.amsterly.lovecoder.lovecoder.model.entity.WeatherEntity;
import com.amsterly.lovecoder.lovecoder.ui.adapter.BaseAdapterData;

/**
 * Created by SilenceDut on 16/10/29.
 */

public class HoursForecastData implements BaseAdapterData {

    public WeatherEntity.HoursForecastEntity hoursForecastData;

    public HoursForecastData(WeatherEntity.HoursForecastEntity hoursForecastData) {
        this.hoursForecastData = hoursForecastData;
    }

    @Override
    public int getItemViewType() {
        return R.layout.item_hour_forecast;
    }
}
