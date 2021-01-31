/*
 * Copyright 2018 Sanket Bhat
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.udacity.sanketbhat.popularmovies.api

import com.udacity.sanketbhat.popularmovies.BuildConfig
import com.udacity.sanketbhat.popularmovies.model.PageResponse
import com.udacity.sanketbhat.popularmovies.model.ReviewResponse
import com.udacity.sanketbhat.popularmovies.model.VideoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDBApiService {
    @GET("{type}?api_key=$API_KEY")
    fun getFirstPage(@Path("type") sortOrderPath: String?): Call<PageResponse?>?

    @GET("{type}?api_key=$API_KEY")
    fun getPage(@Path("type") sortOrderPath: String?, @Query("page") pageNumber: Int): Call<PageResponse?>?

    @GET("{id}/videos?api_key=$API_KEY")
    fun getVideos(@Path("id") id: Int): Call<VideoResponse?>?

    @GET("{id}/reviews?api_key=$API_KEY")
    fun getReviews(@Path("id") id: Int): Call<ReviewResponse?>?

    companion object {
        const val API_KEY = BuildConfig.THE_MOVIE_DB_API_KEY
    }
}