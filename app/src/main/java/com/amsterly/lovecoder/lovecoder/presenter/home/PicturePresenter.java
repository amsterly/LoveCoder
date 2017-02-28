package com.amsterly.lovecoder.lovecoder.presenter.home;


import com.amsterly.lovecoder.lovecoder.view.home.IPicture;
import com.amsterly.lovecoder.lovecoder.presenter.base.BasePresenter;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * create by lvwenbo
 */
public class PicturePresenter extends BasePresenter<IPicture> {
    private IPicture iview;

    public PicturePresenter(IPicture main) {
        this.attachView(main);
        iview = getView();
    }
}
