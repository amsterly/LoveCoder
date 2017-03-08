package com.amsterly.lovecoder.lovecoder.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;

//import com.afollestad.easyvideoplayer.EasyVideoPlayer;
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
import com.amsterly.lovecoder.lovecoder.utils.Once;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class MainActivity extends SwipeRefreshBaseActivity implements AppBarLayout.OnOffsetChangedListener {


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
    //    @Bind(R.id.video_view)
//    VideoView videoView;
    @Bind(R.id.danmaku_view)
    DanmakuView danmakuView;
//    @Bind(R.id.player)
//    EasyVideoPlayer player;

    private ActionBarDrawerToggle mDrawerToggle;
    private final static String TAG = "MainActivity";
    private MeizhiListAdapter mMeizhiListAdapter;
    private List<Meizhi> mMeizhiList;
    private static final int PRELOAD_SIZE = 6;
    private boolean mIsFirstTimeTouchBottom = true;
    public static int mPage = 1;
    private boolean mMeizhiBeTouched;
    //弹幕
    private boolean showDanmaku;


    private DanmakuContext danmakuContext;

    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initParam();
        setupRecyclerView();
        initDanmaku();

    }

    private void initDanmaku() {
//        Uri uri=Uri.parse("android:resource://"+getApplication().getPackageName()+"/"+R.raw.hashiqi);
//        videoView.setVideoPath(uri.toString())  ;
        //http://
//        videoView.setVideoURI(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));
//        videoView.setVideoURI(Uri.parse("http://player.youku.com/player.php/sid/XMjUwOTQzODg3Ng==/v.swf"));
//        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
////                Log.i(TAG, "MediaPlayer onError: "+mp.);
//                return false;
//            }
//        });
//        videoView.start();
//        player.setSource(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));
//        player.start();
        danmakuView.enableDanmakuDrawingCache(true);
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                showDanmaku = true;
                danmakuView.start();
                generateSomeDanmaku();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        danmakuContext = DanmakuContext.create();
        danmakuView.prepare(parser, danmakuContext);
    }

    /**
     * 随机生成一些弹幕内容以供测试
     */
    private void generateSomeDanmaku() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (showDanmaku) {
                    int time = new Random().nextInt(500);
                    String content = "" + time + time;
                    addDanmaku(content, false);
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 向弹幕View中添加一条弹幕
     *
     * @param content    弹幕的具体内容
     * @param withBorder 弹幕是否有边框
     */
    private void addDanmaku(String content, boolean withBorder) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = (20);
        danmaku.textColor = Color.WHITE;
        danmaku.setTime(danmakuView.getCurrentTime());
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        danmakuView.addDanmaku(danmaku);
    }

    private void initParam() {
        mAppBarLayout.addOnOffsetChangedListener(this);

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
    protected void onPause() {
        super.onPause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
//        if (player != null)
//            player.pause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
//        mAppBarLayout.setExpanded(false, true);
//        mAppBarLayout.setExpanded(true, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        showDanmaku = false;
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.loadData(true);
            }
        }, 358);
        ;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {
        } else if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
        }
        mSwipeRefreshLayout.setEnabled(verticalOffset == 0);
        float alpha = (float) Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange() * 1.0f;
        mToolbar.setAlpha(alpha);
    }


    private void setupRecyclerView() {
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(layoutManager);
        mMeizhiListAdapter = new MeizhiListAdapter(this, mMeizhiList);
        recyclerview.setAdapter(mMeizhiListAdapter);
        new Once(this).show("tip_guide_6", () -> {
            Snackbar.make(recyclerview, getString(R.string.tip_guide), Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.i_know, v -> {
                    })
                    .show();
        });
        recyclerview.addOnScrollListener(getOnBottomListener(layoutManager));
        mMeizhiListAdapter.setOnMeizhiTouchListener(getOnMeizhiTouchListener());
    }

    RecyclerView.OnScrollListener getOnBottomListener(final StaggeredGridLayoutManager layoutManager) {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                //当预加载量与最后一个完整看到的Item的位置之和>=每次请求的最大数时 请求加载新数据
                boolean isBottom =
                        layoutManager.findLastCompletelyVisibleItemPositions(new int[2])[1] + PRELOAD_SIZE >=
                                mMeizhiListAdapter.getItemCount();
                if (!mSwipeRefreshLayout.isRefreshing() && isBottom) {
                    if (!mIsFirstTimeTouchBottom) {
                        mSwipeRefreshLayout.setRefreshing(true);
                        MainActivity.mPage += 1;
                        mPresenter.loadData();
                    } else {
                        mIsFirstTimeTouchBottom = false;
                    }
                }
            }
        };
    }


    public static int mLastVideoIndex = 0;

    private void startPictureActivity(Meizhi meizhi, View transitView) {
        Intent intent = PictureActivity.newIntent(MainActivity.this, meizhi.url, meizhi.desc);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                MainActivity.this, transitView, PictureActivity.TRANSIT_PIC);
        try {
            ActivityCompat.startActivity(MainActivity.this, intent, optionsCompat.toBundle());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            startActivity(intent);
        }
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
        mPresenter.loadData(true);
        addDanmaku("人家拿小拳拳捶你胸口！", true);
    }


    @Override
    public List<Meizhi> getMeizhiList() {
        return this.mMeizhiList;
    }

    @Override
    public void notifyDataSetChanged() {
        mMeizhiListAdapter.notifyDataSetChanged();
    }
    @Override
    public void loadError(Throwable throwable) {
        throwable.printStackTrace();
        Snackbar.make(recyclerview, R.string.snap_load_fail, Snackbar.LENGTH_LONG)
//                .setAction(R.string.retry, v -> {
//                    requestDataRefresh();
//                })
                .show();
    }


    private OnMeizhiTouchListener getOnMeizhiTouchListener() {
        return (v, meizhiView, card, meizhi) -> {
            if (meizhi == null) return;
            if (v == meizhiView && !mMeizhiBeTouched) {
                mMeizhiBeTouched = true;
                Picasso.with(this).load(meizhi.url).fetch(new Callback() {

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
                startGankActivity(meizhi.publishedAt);
            }
        };
    }

    private void startGankActivity(Date publishedAt) {
        Intent intent = new Intent(this, GankActivity.class);
        intent.putExtra(GankActivity.EXTRA_GANK_DATE, publishedAt);
        startActivity(intent);
    }


}
