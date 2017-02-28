package com.amsterly.lovecoder.lovecoder.ui.activity.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.amsterly.lovecoder.lovecoder.presenter.base.BasePresenter;
import com.amsterly.lovecoder.lovecoder.utils.SystemBarTintManager;
import com.amsterly.lovecoder.lovecoder.view.base.IBase;


/**
 * Created by fancy on 2016/6/23.
 */
public abstract class BaseActivity<V,T extends BasePresenter<V>> extends AppCompatActivity implements IBase {
    protected T mPresenter;
    protected boolean nowRequest;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter != null)
            mPresenter.dettachView();
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
    }

    protected abstract T createPresenter();

    @Override
    public void showServerError(String s) {
        Toast.makeText(this, "服务器错误:" + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoNetTextView() {
//        Toast.makeText(this, "无可用网络", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTextView(String s) {
    }


    @Override
    public void requestTimeout() {
        Toast.makeText(this, "请求超时", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccessToast(String s) {
        Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean hasNet() {
        //是否有网络
        return true;
    }

    @Override
    public void showProgress(boolean visible) {
        //显示进度条
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public Context getMApplicationContext() {
        return getApplicationContext();
    }

    @Override
    public Context getMContext() {
        return this;
    }

    @Override
    public void setNowRequest(boolean nowRequest) {
        this.nowRequest = nowRequest;
    }
    protected void setStateBarColor(int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(resId);
            tintManager.setStatusBarDarkMode(true, this);
        }
    }


    protected void setUpCommonBackTooblBar(int toolbarId, String title) {
        Toolbar mToolbar = (Toolbar) findViewById(toolbarId);
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);
        toobarAsBackButton(mToolbar);
    }

    protected void setUpCommonBackTooblBar(int toolbarId, int titleId) {
        setUpCommonBackTooblBar(toolbarId, getString(titleId));
    }

    public int getActionBarSize() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return 0;
    }


    public View getRootView() {
        return findViewById(android.R.id.content);
    }


    /**
     * toolbar点击返回，模拟系统返回
     * 设置toolbar 为箭头按钮
     * app:navigationIcon="?attr/homeAsUpIndicator"
     *
     * @param toolbar
     */
    public void toobarAsBackButton(Toolbar toolbar) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}
