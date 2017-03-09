package com.amsterly.lovecoder.lovecoder.ui.adapter;


import com.amsterly.lovecoder.lovecoder.R;
import com.amsterly.lovecoder.lovecoder.model.entity.WeatherEntity;

import java.util.List;

/**
 * Created by SilenceDut on 16/10/25.
 */

public class LifeIndexData implements BaseAdapterData {

    public List<WeatherEntity.LifeIndexEntity> lifeIndexesData;

    public LifeIndexData(List<WeatherEntity.LifeIndexEntity> lifeIndexesData) {
        this.lifeIndexesData = lifeIndexesData;
    }

    @Override
    public int getItemViewType() {
        return R.layout.item_life;
    }
}
