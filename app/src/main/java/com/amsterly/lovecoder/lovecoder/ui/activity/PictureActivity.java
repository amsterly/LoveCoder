package com.amsterly.lovecoder.lovecoder.ui.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.amsterly.lovecoder.lovecoder.R;
import com.amsterly.lovecoder.lovecoder.ui.activity.base.ToolbarActivity;
import com.amsterly.lovecoder.lovecoder.utils.RxMeizhi;
import com.amsterly.lovecoder.lovecoder.utils.Shares;
import com.amsterly.lovecoder.lovecoder.utils.Toasts;
import com.amsterly.lovecoder.lovecoder.view.home.IPicture;
import com.amsterly.lovecoder.lovecoder.presenter.home.PicturePresenter;
import com.amsterly.lovecoder.lovecoder.ui.activity.base.BaseActivity;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * create by lvwenbo
 */
public class PictureActivity extends ToolbarActivity<IPicture, PicturePresenter> implements IPicture {


    public static final String EXTRA_IMAGE_URL = "image_url";
    public static final String EXTRA_IMAGE_TITLE = "image_title";
    public static final String TRANSIT_PIC = "picture";

    @Bind(R.id.picture)
    ImageView mImageView;

    PhotoViewAttacher mPhotoViewAttacher;
    String mImageUrl, mImageTitle;

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_picture;
    }


    @Override
    public boolean canBack() {
        return true;
    }


    public static Intent newIntent(Context context, String url, String desc) {
        Intent intent = new Intent(context, PictureActivity.class);
        intent.putExtra(PictureActivity.EXTRA_IMAGE_URL, url);
        intent.putExtra(PictureActivity.EXTRA_IMAGE_TITLE, desc);
        return intent;
    }


    private void parseIntent() {
        mImageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        mImageTitle = getIntent().getStringExtra(EXTRA_IMAGE_TITLE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        parseIntent();
        ViewCompat.setTransitionName(mImageView, TRANSIT_PIC);
        Picasso.with(this).load(mImageUrl).into(mImageView);
        setAppBarAlpha(0.7f);
        setTitle(mImageTitle);
        setupPhotoAttacher();
    }


    private void setupPhotoAttacher() {
        mPhotoViewAttacher = new PhotoViewAttacher(mImageView);
        mPhotoViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                hideOrShowToolbar();
            }
        });
        // @formatter:off
        mPhotoViewAttacher.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(PictureActivity.this)
                        .setMessage(getString(R.string.ask_saving_picture))
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        saveImageToGallery();
                                        dialog.dismiss();
                                    }
                                })
                        .show();
                return true;
            }
        });
    }


    private void saveImageToGallery() {
        // @formatter:off
        Subscription s = RxMeizhi.saveImageAndGetPathObservable(this, mImageUrl, mImageTitle)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Uri>() {
                    @Override
                    public void call(Uri uri) {
                        File appDir = new File(Environment.getExternalStorageDirectory(), "Meizhi");
                        String msg = String.format(getString(R.string.picture_has_save_to),
                                appDir.getAbsolutePath());
                        Toasts.showShort(msg);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toasts.showLong(throwable.getMessage() + "\n再试试...");
                    }

                });
        // @formatter:on
        addSubscription(s);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picture, menu);
        // TODO: 把图片的一些信息，比如 who，加载到 Overflow 当中
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_share:
                RxMeizhi.saveImageAndGetPathObservable(this, mImageUrl, mImageTitle)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Uri>() {
                                       @Override
                                       public void call(Uri uri) {
                                           Shares.shareImage(PictureActivity.this, uri,
                                                   getString(R.string.share_meizhi_to));
                                       }
                                   },
                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        Toasts.showLong(throwable.getMessage());
                                    }
                                }
                        );
                return true;
            case R.id.action_save:
                saveImageToGallery();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }


    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhotoViewAttacher.cleanup();
        ButterKnife.unbind(this);
    }

    @Override
    protected PicturePresenter createPresenter() {
        return new PicturePresenter(this);
    }
}
