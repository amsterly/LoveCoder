package com.amsterly.lovecoder.lovecoder.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amsterly.lovecoder.lovecoder.App;
import com.amsterly.lovecoder.lovecoder.R;
import com.amsterly.lovecoder.lovecoder.func.OnMeizhiTouchListener;
import com.amsterly.lovecoder.lovecoder.model.MeizhiData;
import com.amsterly.lovecoder.lovecoder.model.entity.Gank;
import com.amsterly.lovecoder.lovecoder.model.entity.Meizhi;
import com.amsterly.lovecoder.lovecoder.model.休息视频Data;
import com.amsterly.lovecoder.lovecoder.presenter.home.MainPresenter;
import com.amsterly.lovecoder.lovecoder.ui.activity.base.SwipeRefreshBaseActivity;
import com.amsterly.lovecoder.lovecoder.ui.adapter.MeizhiListAdapter;
import com.amsterly.lovecoder.lovecoder.utils.Dates;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MainActivity extends SwipeRefreshBaseActivity implements AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.relativeLayout)
    TextView relativeLayout;
    @Bind(R.id.headerView)
    RelativeLayout mHeaderView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.collapse_toolbar)
    CollapsingToolbarLayout mCollapseToolbar;
    @Bind(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;
    //    @Bind(R.id.swipe_container)
//    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.id_navigationView)
    NavigationView mNavigationView;
    @Bind(R.id.id_drawerLayout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.main_fab)
    FloatingActionButton mainFab;
    private ActionBarDrawerToggle mDrawerToggle;
    private final static String TAG = "MainActivity";
    private MeizhiListAdapter mMeizhiListAdapter;
    private List<Meizhi> mMeizhiList;
    private static final int PRELOAD_SIZE = 6;
    private boolean mIsFirstTimeTouchBottom = true;
    private int mPage = 1;
    private boolean mMeizhiBeTouched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAppBarLayout.addOnOffsetChangedListener(this);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, 100);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(
                R.id.collapse_toolbar);
        collapsingToolbar.setTitleEnabled(false);
        //set the mToolbar
//        int toolbar_hight = Utils.getToolbarHeight(this) * 2;
//        CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) mToolbar.getLayoutParams();
//        params.height = toolbar_hight;
//        mToolbar.setLayoutParams(params);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(0);
            mAppBarLayout.setElevation(0);
        }
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mNavigationView = (NavigationView) findViewById(R.id.id_navigationView);
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                mMenuItemID = menuItem.getItemId();
//                initFragmentByMenuItemID(mMenuItemID);
//                mNavigationView.getMenu().clear();
//                mNavigationView.inflateMenu(R.menu.menu_drawer);
//                mNavigationView.getMenu().findItem(mMenuItemID).setChecked(true);
//                initNavigationViewByPermission();
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                return true;
            }
        });
        mMeizhiList = new ArrayList<>();
        QueryBuilder query = new QueryBuilder(Meizhi.class);
        query.appendOrderDescBy("publishedAt");
        query.limit(0, 10);
        if (App.sDb.query(query) != null)
            mMeizhiList.addAll(App.sDb.query(query));
        setupRecyclerView();

    }



    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mAppBarLayout.setExpanded(false, true);
//        mAppBarLayout.setExpanded(true, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData(true);
            }
        }, 358);
        ;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mSwipeRefreshLayout.setEnabled(verticalOffset == 0);
        float alpha = (float) Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange() * 1.0f;
        mToolbar.setAlpha(alpha);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void setupRecyclerView() {
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(layoutManager);
        mMeizhiListAdapter = new MeizhiListAdapter(this, mMeizhiList);
        recyclerview.setAdapter(mMeizhiListAdapter);
//        new Once(this).show("tip_guide_6", () -> {
//            Snackbar.make(mRecyclerView, getString(R.string.tip_guide), Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.i_know, v -> {
//                    })
//                    .show();
//        });
        recyclerview.addOnScrollListener(getOnBottomListener(layoutManager));
        mMeizhiListAdapter.setOnMeizhiTouchListener(getOnMeizhiTouchListener());
    }

    RecyclerView.OnScrollListener getOnBottomListener(final StaggeredGridLayoutManager layoutManager) {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                boolean isBottom =
                        layoutManager.findLastCompletelyVisibleItemPositions(new int[2])[1] >=
                                mMeizhiListAdapter.getItemCount() - PRELOAD_SIZE;
                if (!mSwipeRefreshLayout.isRefreshing() && isBottom) {
                    if (!mIsFirstTimeTouchBottom) {
                        mSwipeRefreshLayout.setRefreshing(true);
                        mPage += 1;
                        loadData();
                    } else {
                        mIsFirstTimeTouchBottom = false;
                    }
                }
            }
        };
    }

    private void loadData() {
        loadData(/* clean */false);
    }

    private void loadData(boolean clean) {
        mLastVideoIndex = 0;
        // @formatter:off
        Subscription s = Observable
                .zip(sGankIO.getMeizhiData(mPage),
                        sGankIO.get休息视频Data(mPage),
                        this::createMeizhiDataWith休息视频Desc)
                .map(new Func1<MeizhiData, List<Meizhi>>() {
                    @Override
                    public List<Meizhi> call(MeizhiData meizhiData) {
                        return meizhiData.results;
                    }
                })
                .flatMap(new Func1<List<Meizhi>, Observable<? extends Meizhi>>() {
                    @Override
                    public Observable<? extends Meizhi> call(List<Meizhi> iterable) {
                        return Observable.from(iterable);
                    }
                })
                .toSortedList(new Func2<Meizhi, Meizhi, Integer>() {
                    @Override
                    public Integer call(Meizhi meizhi1, Meizhi meizhi2) {
                        return meizhi2.publishedAt.compareTo(meizhi1.publishedAt);
                    }
                })
                .doOnNext(new Action1<List<Meizhi>>() {
                    @Override
                    public void call(List<Meizhi> meizhis) {
                        MainActivity.this.saveMeizhis(meizhis);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(new Action0() {
                    @Override
                    public void call() {
                        setRefresh(false);
                    }
                })
                .subscribe(new Action1<List<Meizhi>>() {
                    @Override
                    public void call(List<Meizhi> meizhis) {
                        if (clean) mMeizhiList.clear();
                        mMeizhiList.addAll(meizhis);
                        mMeizhiListAdapter.notifyDataSetChanged();
                        MainActivity.this.setRefresh(false);
                    }
                }, throwable -> loadError(throwable));
        // @formatter:on
        addSubscription(s);
    }

    private int mLastVideoIndex = 0;

    private void startPictureActivity(Meizhi meizhi, View transitView) {
        Intent intent = PictureActivity.newIntent(MainActivity.this, meizhi.url, meizhi.desc);
        startActivity(intent);
//        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                MainActivity.this, transitView, PictureActivity.TRANSIT_PIC);
        try {
//            ActivityCompat.startActivity(MainActivity.this, intent, optionsCompat.toBundle());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
//            startActivity(intent);
        }
    }

    private String getFirstVideoDesc(Date publishedAt, List<Gank> results) {
        String videoDesc = "";
        for (int i = mLastVideoIndex; i < results.size(); i++) {
            Gank video = results.get(i);
            if (video.publishedAt == null) video.publishedAt = video.createdAt;
            if (Dates.isTheSameDay(publishedAt, video.publishedAt)) {
                videoDesc = video.desc;
                mLastVideoIndex = i;
                break;
            }
        }
        return videoDesc;
    }

    public void onToolbarClick() {
        recyclerview.smoothScrollToPosition(0);
    }


    @OnClick(R.id.main_fab)
    public void onFab(View v) {
        if (mMeizhiList != null && mMeizhiList.size() > 0) {
//            startGankActivity(mMeizhiList.get(0).publishedAt);
        }
    }


    @Override
    public void requestDataRefresh() {
        super.requestDataRefresh();
        mPage = 1;
        loadData(true);
    }

    private void saveMeizhis(List<Meizhi> meizhis) {
        App.sDb.insert(meizhis, ConflictAlgorithm.Replace);
    }

    private void loadError(Throwable throwable) {
        throwable.printStackTrace();
        Snackbar.make(recyclerview, R.string.snap_load_fail, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry, v -> {
                    requestDataRefresh();
                })
                .show();
    }

    private MeizhiData createMeizhiDataWith休息视频Desc(MeizhiData data, 休息视频Data love) {
        for (Meizhi meizhi : data.results) {
            meizhi.desc = meizhi.desc + " " +
                    getFirstVideoDesc(meizhi.publishedAt, love.results);
        }
        return data;
    }

    private OnMeizhiTouchListener getOnMeizhiTouchListener() {
        return (new OnMeizhiTouchListener() {
            @Override
            public void onTouch(View v, View meizhiView, View card, Meizhi meizhi) {
                if (meizhi == null) return;
                if (v == meizhiView && !mMeizhiBeTouched) {
                    mMeizhiBeTouched = true;
                    Picasso.with(MainActivity.this).load(meizhi.url).fetch(new Callback() {

                        @Override
                        public void onSuccess() {
                            mMeizhiBeTouched = false;
                            startPictureActivity(meizhi, meizhiView);
                        }


                        @Override
                        public void onError() {
                            mMeizhiBeTouched = false;
                        }
                    });
                } else if (v == card) {
//                startGankActivity(meizhi.publishedAt);
                }
            }
        });
    }

}
