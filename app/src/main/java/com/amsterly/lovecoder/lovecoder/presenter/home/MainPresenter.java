package com.amsterly.lovecoder.lovecoder.presenter.home;


import com.amsterly.lovecoder.lovecoder.view.home.IMain;
import com.amsterly.lovecoder.lovecoder.presenter.base.BasePresenter;

/**
 * create by lvwenbo
 */
public class MainPresenter extends BasePresenter<IMain> {
    private IMain iview;

    public MainPresenter(IMain main) {
        this.attachView(main);
        iview = getView();
    }
}
