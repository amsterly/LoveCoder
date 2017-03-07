package com.amsterly.lovecoder.lovecoder;

import android.app.Application;
import android.content.Context;

import com.amsterly.lovecoder.lovecoder.utils.Toasts;
import com.litesuits.orm.LiteOrm;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by lvwenbo on 2017/3/1.
 */

public class App extends Application {
    private static final String DB_NAME = "lovecoder.db";
    public static Context sContext;
    public static LiteOrm sDb;

    @Override
    public void onCreate() {
        super.onCreate();
        //高效加载字体包
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/NotoSans-Regular.ttf").setFontAttrId(R.attr.fontPath).build());
        sContext = this;
        Toasts.register(this);
        sDb = LiteOrm.newSingleInstance(this, DB_NAME);
        if (BuildConfig.DEBUG) {
            sDb.setDebugged(true);
        }
    }
}
