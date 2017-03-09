package com.amsterly.lovecoder.lovecoder.network;

import com.amsterly.lovecoder.lovecoder.App;
import com.amsterly.lovecoder.lovecoder.utils.Check;
import com.amsterly.lovecoder.lovecoder.utils.FileUtil;
import com.amsterly.lovecoder.lovecoder.utils.NetWork;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by SilenceDut on 16/10/28.
 */

public class AppHttpClient {

    private static final int HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;
    private static final int MAX_AGE = 60 * 10; //with network 10min
    private static final int MAX_STALE = 60 * 60 * 24; //1 day ,no network
    private volatile static AppHttpClient sAppHttpClient;
    private Map<String, Object> serviceByType = new HashMap<>();
    private Retrofit mRetrofit;

    private AppHttpClient() {

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(cacheInterceptor()).cache(cache()).build();

        mRetrofit = new Retrofit.Builder().baseUrl(WeatherApi.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build();

    }


    private Cache cache() {
        final  File baseDir = FileUtil.getExternalCacheDir(App.sContext);
        final File cacheDir = new File(baseDir, "HttpResponseCache");
        return (new Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE));

    }

    private Interceptor cacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();

                if (!NetWork.isAvailable(App.sContext)) {
                    request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                }
                Response originalResponse = chain.proceed(request);

                if (NetWork.isAvailable(App.sContext)) {
                    return originalResponse.newBuilder().header("Cache-Control", "public ,max-age=" + MAX_AGE).build();
                } else {
                    return originalResponse.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + MAX_STALE).build();
                }
            }
        };
    }

    public static AppHttpClient getInstance() {
        if (sAppHttpClient == null) {
            synchronized (AppHttpClient.class) {
                if (sAppHttpClient == null) {
                    sAppHttpClient = new AppHttpClient();
                }
            }
        }
        return sAppHttpClient;
    }

    public synchronized <T> T getService(Class<T> apiInterface) {
        String serviceName = apiInterface.getName();
        if (!Check.isNull(serviceByType.get(serviceName))) {
            return (T) serviceByType.get(serviceName);
        }
        T service = mRetrofit.create(apiInterface);
        serviceByType.put(serviceName, service);
        return service;
    }

}
