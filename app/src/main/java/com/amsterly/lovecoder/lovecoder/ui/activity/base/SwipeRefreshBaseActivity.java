package com.amsterly.lovecoder.lovecoder.ui.activity.base;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;

import com.amsterly.lovecoder.lovecoder.R;
import com.amsterly.lovecoder.lovecoder.network.DrakeetFactory;
import com.amsterly.lovecoder.lovecoder.network.GankApi;
import com.amsterly.lovecoder.lovecoder.presenter.base.BasePresenter;
import com.amsterly.lovecoder.lovecoder.presenter.home.MainPresenter;
import com.amsterly.lovecoder.lovecoder.ui.base.SwipeRefreshLayer;
import com.amsterly.lovecoder.lovecoder.ui.widget.MultiSwipeRefreshLayout;
import com.amsterly.lovecoder.lovecoder.view.base.IBase;
import com.amsterly.lovecoder.lovecoder.view.home.IMain;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by lvwenbo on 2017/2/28.
 */

public  abstract class SwipeRefreshBaseActivity <V,T extends BasePresenter<V>> extends BaseActivity<V,T> implements  SwipeRefreshLayer {
//    @Bind(R.id.swipe_container)
    public MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRequestDataRefresh = false;
    public static final GankApi sGankIO = DrakeetFactory.getGankIOSingleton();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ButterKnife.bind(this);
    }
    @Override public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        ButterKnife.bind(this);
//        mSwipeRefreshLayout.setProgressViewOffset(false,100,300);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        trySetupSwipeRefresh();
    }



    void trySetupSwipeRefresh() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setProgressViewOffset(false, 100, 300);
            mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
//            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_3,
                    R.color.refresh_progress_2, R.color.refresh_progress_1);
            // Do not use lambda here!
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override public void onRefresh() {
                    requestDataRefresh();
                }
            });
        }
    }

    @Override
    public void requestDataRefresh() {
        mIsRequestDataRefresh = true;
    }

    public void setRefresh(boolean requestDataRefresh) {
        if (mSwipeRefreshLayout == null) {
            return;
        }
        if (!requestDataRefresh) {
            mIsRequestDataRefresh = false;
            // 防止刷新消失太快，让子弹飞一会儿.
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override public void run() {
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }, 1000);
        } else {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override public void setProgressViewOffset(boolean scale, int start, int end) {
        mSwipeRefreshLayout.setProgressViewOffset(scale, start, end);
//        mSwipeRefreshLayout.setProgressViewOffset(false,100,300);
    }


    @Override
    public void setCanChildScrollUpCallback(MultiSwipeRefreshLayout.CanChildScrollUpCallback canChildScrollUpCallback) {
        mSwipeRefreshLayout.setCanChildScrollUpCallback(canChildScrollUpCallback);
    }


    public boolean isRequestDataRefresh() {
        return mIsRequestDataRefresh;
    }


}
