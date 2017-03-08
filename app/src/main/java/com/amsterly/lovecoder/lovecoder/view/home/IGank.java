package com.amsterly.lovecoder.lovecoder.view.home;

import com.amsterly.lovecoder.lovecoder.model.entity.Gank;
import com.amsterly.lovecoder.lovecoder.ui.widget.VideoImageView;
import com.amsterly.lovecoder.lovecoder.view.base.IBase;

import java.util.List;

/**
 * Created by lvwenbo on 2017/3/8.
 */

public interface IGank extends IBase {
    int getYear();

    int getMonth();

    int getDay();

    void notifyDataSetChanged();

    void showEmptyView();

    List<Gank> getGankList();

    VideoImageView getVideoImageView();


}
