package com.amsterly.lovecoder.lovecoder.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amsterly.lovecoder.lovecoder.App;
import com.amsterly.lovecoder.lovecoder.R;
import com.amsterly.lovecoder.lovecoder.func.OnMeizhiTouchListener;
import com.amsterly.lovecoder.lovecoder.model.HoursForecastData;
import com.amsterly.lovecoder.lovecoder.model.entity.Meizhi;
import com.amsterly.lovecoder.lovecoder.model.entity.WeatherEntity;
import com.amsterly.lovecoder.lovecoder.presenter.home.MainPresenter;
import com.amsterly.lovecoder.lovecoder.ui.activity.base.SwipeRefreshBaseActivity;
import com.amsterly.lovecoder.lovecoder.ui.adapter.AqiData;
import com.amsterly.lovecoder.lovecoder.ui.adapter.BaseRecyclerAdapter;
import com.amsterly.lovecoder.lovecoder.ui.adapter.DailyWeatherData;
import com.amsterly.lovecoder.lovecoder.ui.adapter.HourWeatherHolder;
import com.amsterly.lovecoder.lovecoder.ui.adapter.LifeIndexData;
import com.amsterly.lovecoder.lovecoder.ui.adapter.MeizhiListAdapter;
import com.amsterly.lovecoder.lovecoder.ui.widget.AqiView;
import com.amsterly.lovecoder.lovecoder.ui.widget.MultiSwipeRefreshLayout;
import com.amsterly.lovecoder.lovecoder.ui.widget.dynamicweather.BaseDrawer;
import com.amsterly.lovecoder.lovecoder.ui.widget.dynamicweather.DynamicWeatherView;
import com.amsterly.lovecoder.lovecoder.utils.Once;
import com.amsterly.lovecoder.lovecoder.view.home.IMain;
import com.litesuits.orm.db.assit.QueryBuilder;
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

//import com.afollestad.easyvideoplayer.EasyVideoPlayer;

public class MainActivity extends SwipeRefreshBaseActivity<IMain, MainPresenter> implements IMain, AppBarLayout.OnOffsetChangedListener {


    //    @Bind(R.id.headerView)
//    RelativeLayout mHeaderView;
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

    @Bind(R.id.main_dynamicweatherview)
    DynamicWeatherView mainDynamicweatherview;
    @Bind(R.id.main_hours_forecast_recyclerView)
    RecyclerView mainHoursForecastRecyclerView;
    @Bind(R.id.main_temp)
    TextView mainTemp;
    @Bind(R.id.main_info)
    TextView mainInfo;
    @Bind(R.id.w_aqi_view)
    AqiView wAqiView;
    @Bind(R.id.container_layout)
    LinearLayout containerLayout;
    @Bind(R.id.danmaku_view)
    DanmakuView danmakuView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_temperature)
    TextView toolbarTemperature;
    @Bind(R.id.toolbar_quality)
    TextView toolbarQuality;
    @Bind(R.id.toolbar_city)
    TextView toolbarCity;
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
    private BaseRecyclerAdapter mHoursForecastAdapter;
    private String mTemperature;
    private String mWeatherStatus;
    private float mXPos_Container = 0;
    private float mXPos_Aqi = 0;
    @Bind(R.id.swipe_container)
    public MultiSwipeRefreshLayout mSwipeRefreshLayout;


    public static Typeface typeface;
    private boolean mIsRequestDataRefresh=false;

    public static Typeface getTypeface(Context context) {
        return typeface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        trySetupSwipeRefresh();
        initParam();
        setupRecyclerView();
        setupHoursForecast();
        initDanmaku();

    }
    void trySetupSwipeRefresh() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setProgressViewOffset(false, 100, 300);
            mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
//            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_3,
                    R.color.refresh_progress_2, R.color.refresh_progress_1);
            // Do not use lambda here!
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override public void onRefresh() {
                    requestDataRefresh();
                }
            });
        }
    }
    @Override
    public void setRefresh(boolean requestDataRefresh) {
        if (mSwipeRefreshLayout == null) {
            return;
        }
        if (!requestDataRefresh) {
            mIsRequestDataRefresh = false;
            // 防止刷新消失太快，让子弹飞一会儿.
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override public void run() {
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }, 1000);
        } else {
            mSwipeRefreshLayout.setRefreshing(true);
        }
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
                    int time = new Random().nextInt(2000);
                    String content = "" + time + time;
                    if(time%2==0)
                        content = "天气寒冷注意增减衣服";
                    else
                        content = "这还是北京么？都能看到对岸";
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
        if (typeface == null) {
            typeface = Typeface.createFromAsset(getAssets(), "fonts/mxx_font2.ttf");
        }

        mainDynamicweatherview.setDrawerType(BaseDrawer.Type.RAIN_SNOW_D);
        mAppBarLayout.addOnOffsetChangedListener(this);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(
                R.id.collapse_toolbar);
        collapsingToolbar.setTitleEnabled(false);
        //set the mToolbar
//        int toolbar_hight = Utils.getToolbarHeight(this) * 2;
//        CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) mToolbar.getLayoutParams();
//        params.height = toolbar_hight;
//        mToolbar.setLayoutParams(params);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(0);
            mAppBarLayout.setElevation(0);
        }
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open,
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
            mMeizhiList.addAll(App.sDb.<Meizhi>query(query));
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
        mainDynamicweatherview.onPause();
//        if (player != null)
//            player.pause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
        mainDynamicweatherview.onResume();
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
        mainDynamicweatherview.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.loadData(true);

//                mPresenter.updateDefaultWeather();
            }
        }, 358);
        ;

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (Math.abs(verticalOffset) > appBarLayout.getTotalScrollRange() / 2) {
            mainTemp.setVisibility(View.INVISIBLE);
            mainInfo.setVisibility(View.INVISIBLE);
            wAqiView.setVisibility(View.INVISIBLE);
            toolbarQuality.setVisibility(View.VISIBLE);
            toolbarTemperature.setVisibility(View.VISIBLE);
            showDanmaku=false;
//            danmakuView.stop();
        }//Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()
        else {
            mainTemp.setVisibility(View.VISIBLE);
            mainInfo.setVisibility(View.VISIBLE);
            wAqiView.setVisibility(View.VISIBLE);
            toolbarQuality.setVisibility(View.INVISIBLE);
            toolbarTemperature.setVisibility(View.INVISIBLE);
//            danmakuView.start();
            showDanmaku=true;
        }

        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setEnabled(verticalOffset == 0);
        float alpha = (float) Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange() * 1.0f;
//        if (toolbar != null)
//            toolbar.setAlpha(alpha);
        containerLayout.setAlpha(1 - alpha);
        containerLayout.setScaleX(1 - alpha);
        containerLayout.setScaleY(1 - alpha);
        if (mXPos_Container == 0)
            mXPos_Container = containerLayout.getX();
        containerLayout.setX(mXPos_Container * (1 - alpha));
        wAqiView.setAlpha(1 - alpha);
        wAqiView.setScaleX(1 - alpha);
        wAqiView.setScaleY(1 - alpha);
        if (mXPos_Aqi == 0)
            mXPos_Aqi = wAqiView.getX();
        wAqiView.setX(mXPos_Aqi * (1 - alpha));
    }


    private void setupRecyclerView() {
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(layoutManager);
        mMeizhiListAdapter = new MeizhiListAdapter(this, mMeizhiList);
        recyclerview.setAdapter(mMeizhiListAdapter);
        new Once(this).show("tip_guide_6", new Once.OnceCallback() {
            @Override
            public void onOnce() {
                Snackbar.make(recyclerview, getString(R.string.tip_guide), Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.i_know, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
            }
        });
        recyclerview.addOnScrollListener(getOnBottomListener(layoutManager));
        mMeizhiListAdapter.setOnMeizhiTouchListener(getOnMeizhiTouchListener());
    }

    private void setupHoursForecast() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mainHoursForecastRecyclerView.setLayoutManager(linearLayoutManager);
        mHoursForecastAdapter = new BaseRecyclerAdapter(this);
        mHoursForecastAdapter.registerHolder(HourWeatherHolder.class, R.layout.item_hour_forecast);
        mainHoursForecastRecyclerView.setAdapter(mHoursForecastAdapter);
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

    @Override
    public void onBasicInfo(WeatherEntity.BasicEntity basicData, List<HoursForecastData> hoursForecastDatas, boolean isLocationCity) {
//        mLocationTv.setCompoundDrawables(isLocationCity ? mDrawableLocation : null, null, null, null);
//        mLocationTv.setText(basicData.getCity());
//
//        updateSucceed(String.format(getString(R.string.post), TimeUtil.getTimeTips(basicData.getTime())));

        mTemperature = basicData.getTemp();
        mWeatherStatus = basicData.getWeather();
        mainTemp.setText(mTemperature);
        mainInfo.setText(mWeatherStatus);
        toolbarTemperature.setText(mTemperature);


//
//        if (TimeUtil.isNight()) {
//            if (Constants.sunny(mWeatherStatus)) {
//                mMainBgIv.setImageResource(R.mipmap.bg_night);
//            } else {
//                mMainBgIv.setImageResource(R.mipmap.bg_night_dark);
//            }
//        } else {
//            mMainBgIv.setImageResource(R.mipmap.bg_day);
//        }

        mHoursForecastAdapter.setData(hoursForecastDatas);
    }

    @Override
    public void onMoreInfo(AqiData aqiData, List<DailyWeatherData> dailyForecastDatas, LifeIndexData lifeIndexData) {
        wAqiView.setQuality(aqiData.aqiData.getAqi().equals("") ? -1 : Float.parseFloat(aqiData.aqiData.getAqi()), aqiData.aqiData.getQuality());
        toolbarQuality.setText(aqiData.aqiData.getAqi().equals("") ?"":aqiData.aqiData.getAqi());
    }

    @Override
    public void onRefreshing(boolean refreshing) {

    }


    private OnMeizhiTouchListener getOnMeizhiTouchListener() {
        return new OnMeizhiTouchListener() {
            @Override
            public void onTouch(final View v, final View meizhiView, final View card, final Meizhi meizhi) {
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
                    startGankActivity(meizhi.publishedAt);
                }
            }

            ;

        };
    }

    private void startGankActivity(Date publishedAt) {
        Intent intent = new Intent(this, GankActivity.class);
        intent.putExtra(GankActivity.EXTRA_GANK_DATE, publishedAt);
        startActivity(intent);
    }


}
