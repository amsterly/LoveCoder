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

package com.amsterly.lovecoder.lovecoder.network;


import com.amsterly.lovecoder.lovecoder.model.DGankData;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

// @formatter:off


public interface DrakeetApi {

    @Headers({ "X-LC-Id: 0azfScvBLCC9tAGRAwIhcC40",
               "X-LC-Key: gAuE93qAusvP8gk1VW8DtOUb",
               "Content-Type: application/json" })
    @GET("Gank?limit=1") Observable<DGankData> getDGankData(
            @Query("where") String where);// format {"tag":"2015-11-10"}
}
