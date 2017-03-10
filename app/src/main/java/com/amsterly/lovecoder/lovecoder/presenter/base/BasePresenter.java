package com.amsterly.lovecoder.lovecoder.presenter.base;

import android.util.Log;

import com.silencedut.router.Router;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
/**
 * Created by fancy on 2016/6/22.
 */
public abstract class BasePresenter<T> {
    String TAG = "BasePresenter";

    //view接口类型的弱引用
    protected Reference<T> mViewRef;

    private CompositeSubscription mCompositeSubscription;

    public void attachView(T view) {
        Log.d(TAG, "attachView: "+mViewRef);
        mViewRef = new WeakReference<T>(view);
        Router.instance().register(this);
        Log.d(TAG, "attachView: "+mViewRef);

    }

    protected T getView() {
        return mViewRef.get();
    }

    public boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    public void dettachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
        Router.instance().unregister(this);
    }

    //添加一个Subscription
    protected void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    //解除订阅
    public void unsubcrible() {
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
    }
}
