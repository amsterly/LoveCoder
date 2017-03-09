package com.amsterly.lovecoder.lovecoder.model.weather;



import com.amsterly.lovecoder.lovecoder.App;
import com.amsterly.lovecoder.lovecoder.model.ModelManager;
import com.amsterly.lovecoder.lovecoder.model.callbacks.ModelCallback;
import com.amsterly.lovecoder.lovecoder.model.callbacks.WeatherCallBack;
import com.amsterly.lovecoder.lovecoder.model.entity.WeatherEntity;
import com.amsterly.lovecoder.lovecoder.network.AppHttpClient;
import com.amsterly.lovecoder.lovecoder.network.Constants;
import com.amsterly.lovecoder.lovecoder.network.WeatherApi;
import com.amsterly.lovecoder.lovecoder.utils.LogHelper;
import com.amsterly.lovecoder.lovecoder.utils.PreferencesUtil;
import com.silencedut.router.Router;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SilenceDut on 2016/11/15 .
 */

public class WeatherModel extends BaseModel {
    private WeatherApi mWeatherApiService;
    private WeatherEntity mCachedWeather = null;

    public void onCreate() {
        mWeatherApiService = AppHttpClient.getInstance().getService(WeatherApi.class);
        mCachedWeather = initWeather();
    }

    public WeatherEntity getCachedWeather() {
        return mCachedWeather;
    }


    private WeatherEntity initWeather() {
        WeatherEntity weatherEntity = null;
        String mainPageCache = PreferencesUtil.get(Constants.MAIN_PAGE_WEATHER, Constants.DEFAULT_STR);
        if (!mainPageCache.equals(Constants.DEFAULT_STR)) {
            weatherEntity = App.getGson().fromJson(mainPageCache, WeatherEntity.class);
        }
        return weatherEntity;
    }


    public void updateDefaultWeather() {

        String defaultCity = ModelManager.getModel(CityModel.class).getDefaultId();
        updateWeather(defaultCity);
    }


    public void updateWeather(final String cityId) {

//        Router.instance().getReceiver(MainView.class).onRefreshing(true);

        Call<WeatherEntity> weatherEntityCall = mWeatherApiService.getWeather(cityId);
        weatherEntityCall.enqueue(new Callback<WeatherEntity>() {
            @Override
            public void onResponse(Call<WeatherEntity> call, Response<WeatherEntity> response) {
                WeatherEntity weatherEntity = response.body();
                if (response.isSuccessful() && weatherEntity != null) {
                    String cache = App.getGson().toJson(weatherEntity);
                    PreferencesUtil.put(Constants.MAIN_PAGE_WEATHER, cache);
                    mCachedWeather = weatherEntity;
                    onWeatherEntity(weatherEntity);
                    ModelManager.getModel(CityModel.class).setDefaultId(cityId);
                }
            }

            @Override
            public void onFailure(Call<WeatherEntity> call, Throwable t) {
                LogHelper.e(t, call.toString() + t.getMessage());
                onWeatherEntity(null);
            }
        });

    }

    private void onWeatherEntity(WeatherEntity weatherEntity) {

        Router.instance().getReceiver(ModelCallback.WeatherResult.class).onWeather(weatherEntity);
        Router.instance().getReceiver(WeatherCallBack.NotificationStatus.class).onUpdateNotification();
    }
}
