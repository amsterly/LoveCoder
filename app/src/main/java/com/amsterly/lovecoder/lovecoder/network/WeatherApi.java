package com.amsterly.lovecoder.lovecoder.network;



import com.amsterly.lovecoder.lovecoder.model.entity.WeatherEntity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by SilenceDut on 16/10/28.
 */

public interface WeatherApi {

    String BASE_URL = "http://knowweather.duapp.com";

    @GET("/v1.0/weather/{cityId}")
    Call<WeatherEntity> getWeather(@Path("cityId") String cityId);
}
