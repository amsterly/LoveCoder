package com.amsterly.lovecoder.lovecoder.presenter.home;


import com.amsterly.lovecoder.lovecoder.App;
import com.amsterly.lovecoder.lovecoder.model.MeizhiData;
import com.amsterly.lovecoder.lovecoder.model.entity.Gank;
import com.amsterly.lovecoder.lovecoder.model.entity.Meizhi;
import com.amsterly.lovecoder.lovecoder.model.休息视频Data;
import com.amsterly.lovecoder.lovecoder.ui.activity.MainActivity;
import com.amsterly.lovecoder.lovecoder.utils.Dates;
import com.amsterly.lovecoder.lovecoder.view.home.IMain;
import com.amsterly.lovecoder.lovecoder.presenter.base.BasePresenter;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;


import static com.amsterly.lovecoder.lovecoder.ui.activity.base.SwipeRefreshBaseActivity.sGankIO;

/**
 * create by lvwenbo
 */
public class MainPresenter extends BasePresenter<IMain> {
    private IMain iview;

    public MainPresenter(IMain main) {
        this.attachView(main);
        iview = getView();
    }

    public void loadData() {
        loadData(/* clean */false);
    }

    public void loadData(boolean clean) {
        MainActivity.mLastVideoIndex = 0;
        // @formatter:off
        Subscription s = Observable
                .zip(sGankIO.getMeizhiData(MainActivity.mPage),
                        sGankIO.get休息视频Data(MainActivity.mPage),
                        this::createMeizhiDataWith休息视频Desc)//将视频的Decs分别加入每个图片的字段中
                .map(new Func1<MeizhiData, List<Meizhi>>() {
                    @Override
                    public List<Meizhi> call(MeizhiData meizhiData) {
                        return meizhiData.results;
                    }
                })
                .flatMap(new Func1<List<Meizhi>, Observable<? extends Meizhi>>() {//遍历
                    @Override
                    public Observable<? extends Meizhi> call(List<Meizhi> iterable) {
                        return Observable.from(iterable);
                    }
                })
                .toSortedList(new Func2<Meizhi, Meizhi, Integer>() {//按发表日期排序
                    @Override
                    public Integer call(Meizhi meizhi1, Meizhi meizhi2) {
                        return meizhi2.publishedAt.compareTo(meizhi1.publishedAt);
                    }
                })
                .doOnNext(new Action1<List<Meizhi>>() {//图片缓存存入数据库
                    @Override
                    public void call(List<Meizhi> meizhis) {
                        saveMeizhis(meizhis);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(new Action0() {
                    @Override
                    public void call() {
                        getView().setRefresh(false);
                    }
                })
                .subscribe(new Action1<List<Meizhi>>() {
                    @Override
                    public void call(List<Meizhi> meizhis) {
                        if (clean) getView().getMeizhiList().clear();
                        getView().getMeizhiList().addAll(meizhis);
                        getView().notifyDataSetChanged();
                        getView().setRefresh(false);
                    }
                }, throwable -> getView().loadError(throwable));
        // @formatter:on
        addSubscription(s);
    }

    private MeizhiData createMeizhiDataWith休息视频Desc(MeizhiData data, 休息视频Data love) {
        for (Meizhi meizhi : data.results) {
            meizhi.desc = meizhi.desc + " " +
                    getFirstVideoDesc(meizhi.publishedAt, love.results);
        }
        return data;
    }

    private void saveMeizhis(List<Meizhi> meizhis) {
        App.sDb.deleteAll(Meizhi.class);
        App.sDb.insert(meizhis, ConflictAlgorithm.Replace);
    }

    private String getFirstVideoDesc(Date publishedAt, List<Gank> results) {
        String videoDesc = "";
        for (int i = MainActivity.mLastVideoIndex; i < results.size(); i++) {
            Gank video = results.get(i);
            if (video.publishedAt == null) video.publishedAt = video.createdAt;
            if (Dates.isTheSameDay(publishedAt, video.publishedAt)) {
                videoDesc = video.desc;
                MainActivity.mLastVideoIndex = i;
                break;
            }
        }
        return videoDesc;
    }
}
