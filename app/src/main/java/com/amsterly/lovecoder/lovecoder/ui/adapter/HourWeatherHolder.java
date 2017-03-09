package com.amsterly.lovecoder.lovecoder.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsterly.lovecoder.lovecoder.R;
import com.amsterly.lovecoder.lovecoder.model.HoursForecastData;
import com.amsterly.lovecoder.lovecoder.model.entity.WeatherEntity;
import com.amsterly.lovecoder.lovecoder.network.Constants;
import com.amsterly.lovecoder.lovecoder.utils.Check;

import butterknife.Bind;

/**
 * Created by SilenceDut on 16/10/21.
 */

public class HourWeatherHolder extends BaseViewHolder<HoursForecastData> {
    @Bind(R.id.hour)
    TextView hour;
    @Bind(R.id.hour_icon)
    ImageView hourIcon;
    @Bind(R.id.hour_temp)
    TextView hourTemp;

    public HourWeatherHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
    }

    @Override
    public void updateItem(HoursForecastData data, int position) {
        WeatherEntity.HoursForecastEntity hoursForecastData = data.hoursForecastData;
        if (Check.isNull(hoursForecastData)) {
            return;
        }
        hour.setText(hoursForecastData.getTime().substring(11, 16));
        hourIcon.setImageResource(Constants.getIconId(hoursForecastData.getWeather()));
        hourTemp.setText(hoursForecastData.getTemp());
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_hour_forecast;
    }

}
