/*
 * Copyright (C) 2015 Drakeet <drakeet.me@gmail.com>
 *
 * This file is part of Meizhi
 *
 * Meizhi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Meizhi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Meizhi.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.amsterly.lovecoder.lovecoder.func;

import android.view.View;

import com.amsterly.lovecoder.lovecoder.model.entity.Meizhi;



public interface OnMeizhiTouchListener {
    /***
     *
     * @param v 当前view
     * @param meizhiView 图片View
     * @param card 外部卡片View
     * @param meizhi bean
     */
    void onTouch(View v, View meizhiView, View card, Meizhi meizhi);
}
