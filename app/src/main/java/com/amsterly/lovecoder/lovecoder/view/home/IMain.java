package com.amsterly.lovecoder.lovecoder.view.home;


import com.amsterly.lovecoder.lovecoder.model.entity.Meizhi;
import com.amsterly.lovecoder.lovecoder.view.base.IBase;

import java.util.List;

/**
 * create by  lvwenbo
 */
public interface IMain extends IBase {
    List<Meizhi> getMeizhiList();
    void setRefresh(boolean refresh);
    void notifyDataSetChanged();
    void loadError(Throwable throwable);
}
