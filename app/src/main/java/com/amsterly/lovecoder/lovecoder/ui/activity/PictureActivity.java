package com.amsterly.lovecoder.lovecoder.ui.activity;


import android.os.Bundle;

import com.amsterly.lovecoder.lovecoder.R;
import com.amsterly.lovecoder.lovecoder.view.home.IPicture;
import com.amsterly.lovecoder.lovecoder.presenter.home.PicturePresenter;
import com.amsterly.lovecoder.lovecoder.ui.activity.base.BaseActivity;

/**
 * create by lvwenbo
 */
public class PictureActivity extends BaseActivity<IPicture, PicturePresenter> implements IPicture {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);


    }

    @Override
    protected PicturePresenter createPresenter() {
        return new PicturePresenter(this);
    }
}
