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

package com.udacity.sanketbhat.popularmovies.api;

import com.udacity.sanketbhat.popularmovies.BuildConfig;
import com.udacity.sanketbhat.popularmovies.model.Movie;
import com.udacity.sanketbhat.popularmovies.model.PageResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDBApiService {
    String API_KEY = BuildConfig.THE_MOVIE_DB_API_KEY;

    @GET("{type}?api_key=" + API_KEY)
    Call<PageResponse> getFirstPage(@Path("type") String sortOrderPath);

    @GET("{type}?api_key=" + API_KEY)
    Call<PageResponse> getPage(@Path("type") String sortOrderPath, @Query("page") int pageNumber);

    @GET("{id}?api_key=" + API_KEY + "&append_to_response=videos,reviews")
    Call<Movie> getMovie(@Path("id") int id);
}
