package com.amsterly.lovecoder.lovecoder.view.home;

import com.amsterly.lovecoder.lovecoder.model.weather.CityInfoData;
import com.amsterly.lovecoder.lovecoder.view.base.IBase;

import java.util.List;

/**
 * Created by lvwenbo on 2017/3/9.
 */

public interface ISearchCityView extends IBase {
    void onMatched(List<CityInfoData> cityInfoDatas);

    void onAllCities(List<CityInfoData> allInfoDatas);
}
