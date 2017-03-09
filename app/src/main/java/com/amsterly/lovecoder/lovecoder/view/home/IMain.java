package com.amsterly.lovecoder.lovecoder.view.home;


import com.amsterly.lovecoder.lovecoder.model.HoursForecastData;
import com.amsterly.lovecoder.lovecoder.model.entity.Meizhi;
import com.amsterly.lovecoder.lovecoder.model.entity.WeatherEntity;
import com.amsterly.lovecoder.lovecoder.ui.adapter.AqiData;
import com.amsterly.lovecoder.lovecoder.ui.adapter.DailyWeatherData;
import com.amsterly.lovecoder.lovecoder.ui.adapter.LifeIndexData;
import com.amsterly.lovecoder.lovecoder.view.base.IBase;

import java.util.List;

/**
 * create by  lvwenbo
 */
public interface IMain extends IBase {
    List<Meizhi> getMeizhiList();
    void setRefresh(boolean refresh);
    void notifyDataSetChanged();
    void loadError(Throwable throwable);
    //
    void onBasicInfo(WeatherEntity.BasicEntity basicData, List<HoursForecastData> hoursForecastDatas, boolean isLocationCity);

    void onMoreInfo(AqiData aqiData, List<DailyWeatherData> dailyForecastDatas, LifeIndexData lifeIndexData);

    void onRefreshing(boolean refreshing);
}
