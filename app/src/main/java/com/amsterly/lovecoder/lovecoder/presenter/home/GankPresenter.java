package com.amsterly.lovecoder.lovecoder.presenter.home;

import com.amsterly.lovecoder.lovecoder.model.GankData;
import com.amsterly.lovecoder.lovecoder.model.entity.Gank;
import com.amsterly.lovecoder.lovecoder.network.DrakeetFactory;
import com.amsterly.lovecoder.lovecoder.presenter.base.BasePresenter;
import com.amsterly.lovecoder.lovecoder.ui.activity.base.SwipeRefreshBaseActivity;
import com.amsterly.lovecoder.lovecoder.ui.fragment.GankFragment;
import com.amsterly.lovecoder.lovecoder.utils.LoveStrings;
import com.amsterly.lovecoder.lovecoder.view.home.IGank;
import com.amsterly.lovecoder.lovecoder.view.home.IMain;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by lvwenbo on 2017/3/8.
 */

public class GankPresenter extends BasePresenter<IGank> {
    private IGank iview;
//    private String mVideoPreviewUrl;

    public GankPresenter(IGank main) {
        this.attachView(main);
        iview = getView();
    }

    public void loadData() {
        loadVideoPreview();
        // @formatter:off
        Subscription mSubscription = SwipeRefreshBaseActivity.sGankIO
                .getGankData(getView().getYear(), getView().getMonth(), getView().getDay())
                .map(data -> data.results)
                .map(this::addAllResults)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    if (list.isEmpty()) {
                        showEmptyView();
                    } else {
                        getView().notifyDataSetChanged();

                    }
                }, Throwable::printStackTrace);
        // @formatter:on
    }


    public  void loadVideoPreview() {
        String where = String.format("{\"tag\":\"%d-%d-%d\"}", getView().getYear(), getView().getMonth(), getView().getDay());
        DrakeetFactory.getDrakeetSingleton()
                .getDGankData(where)
                .map(dGankData -> dGankData.results)
                .single(dGanks -> dGanks.size() > 0)
                .map(dGanks -> dGanks.get(0))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dGank -> startPreview(dGank.preview),
                        throwable -> getOldVideoPreview(new OkHttpClient()));
    }

    public  void getOldVideoPreview(OkHttpClient client) {
        String url = "http://gank.io/" + String.format("%s/%s/%s", getView().getYear(), getView().getMonth(), getView().getDay());
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
              GankFragment.mVideoPreviewUrl = LoveStrings.getVideoPreviewImageUrl(body);
                startPreview( GankFragment.mVideoPreviewUrl);
            }
        });
    }


    public  void startPreview(String preview) {
        GankFragment.mVideoPreviewUrl = preview;
        if (preview != null && getView().getVideoImageView() != null) {
            // @formatter:off
            getView().getVideoImageView().post(() ->
                    Glide.with(getView().getVideoImageView().getContext())
                            .load(preview)
                            .into(getView().getVideoImageView()));
            // @formatter:on
        }
    }


    public  void showEmptyView()  {getView().showEmptyView();

    }


    public  List<Gank> addAllResults(GankData.Result results) {
        if (results.androidList != null) getView().getGankList().addAll(results.androidList);
        if (results.iOSList != null) getView().getGankList().addAll(results.iOSList);
        if (results.appList != null) getView().getGankList().addAll(results.appList);
        if (results.拓展资源List != null) getView().getGankList().addAll(results.拓展资源List);
        if (results.瞎推荐List != null) getView().getGankList().addAll(results.瞎推荐List);
        if (results.休息视频List != null) getView().getGankList().addAll(0, results.休息视频List);
        return getView().getGankList();
    }
}
