package com.amsterly.lovecoder.lovecoder.ui.adapter;


import com.amsterly.lovecoder.lovecoder.R;
import com.amsterly.lovecoder.lovecoder.model.entity.WeatherEntity;

/**
 * Created by SilenceDut on 16/10/20.
 */

public class AqiData implements BaseAdapterData {

    public WeatherEntity.AqiEntity aqiData;

    public AqiData(WeatherEntity.AqiEntity aqiData) {
        this.aqiData = aqiData;
    }


    @Override
    public int getItemViewType() {
        return R.layout.item_aqi;
    }
}
