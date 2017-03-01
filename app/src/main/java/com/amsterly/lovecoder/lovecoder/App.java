package com.amsterly.lovecoder.lovecoder;

import android.app.Application;
import android.content.Context;

import com.amsterly.lovecoder.lovecoder.utils.Toasts;
import com.litesuits.orm.LiteOrm;

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
        sContext = this;
        Toasts.register(this);
        sDb = LiteOrm.newSingleInstance(this, DB_NAME);
        if (BuildConfig.DEBUG) {
            sDb.setDebugged(true);
        }
    }
}
