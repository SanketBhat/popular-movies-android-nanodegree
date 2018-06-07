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

package com.udacity.sanketbhat.popularmovies.ui;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.udacity.sanketbhat.popularmovies.api.TheMovieDBApiService;
import com.udacity.sanketbhat.popularmovies.model.Movie;
import com.udacity.sanketbhat.popularmovies.model.PageResponse;
import com.udacity.sanketbhat.popularmovies.model.SortOrder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieListViewModel extends ViewModel {

    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 15000;
    private static final int SCROLL_THRESHOLD = 4;
    private int currentPage = 0;
    private int totalPage = 0;
    private MutableLiveData<List<Movie>> movies;
    private MutableLiveData<Void> failureIndicator;
    private MutableLiveData<Boolean> loadingIndicator;
    private int sortOrder;
    private TheMovieDBApiService service;


    public MovieListViewModel() {
        initialize();
    }

    private void initialize() {
        if (movies == null)
            movies = new MutableLiveData<>();

        if (service == null)
            service = getTheMovieDBApiService();

        if (failureIndicator == null)
            failureIndicator = new MutableLiveData<>();

        if (loadingIndicator == null) {
            loadingIndicator = new MutableLiveData<>();
            loadingIndicator.setValue(false);
        }
    }

    public void getFirstPage(int sortOrder, boolean retrying) {

        if (this.sortOrder == sortOrder && !retrying) return;
        this.sortOrder = sortOrder;

        Call<PageResponse> call = service.getFirstPage(SortOrder.getSortOrderPath(sortOrder));
        call.enqueue(new Callback<PageResponse>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse> call, @NonNull Response<PageResponse> response) {
                if (response.isSuccessful()) {
                    PageResponse pageResponse = response.body();
                    if (pageResponse != null) {
                        currentPage = pageResponse.getPage();
                        totalPage = pageResponse.getTotalPages();
                        movies.setValue(pageResponse.getMovies());
                    }
                } else {
                    failureIndicator.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PageResponse> call, @NonNull Throwable t) {
                failureIndicator.setValue(null);
            }
        });
    }


    private void getNextPage() {
        if (loadingIndicator.getValue() != null && loadingIndicator.getValue()) return;

        loadingIndicator.setValue(true);
        service.getPage(SortOrder.getSortOrderPath(sortOrder), currentPage + 1).enqueue(new Callback<PageResponse>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse> call, @NonNull Response<PageResponse> response) {
                loadingIndicator.setValue(false);
                if (response.isSuccessful()) {
                    PageResponse pageResponse = response.body();
                    if (pageResponse != null) {
                        currentPage = pageResponse.getPage();
                        totalPage = pageResponse.getTotalPages();
                        if (movies.getValue() != null)
                            movies.getValue().addAll(pageResponse.getMovies());
                    }
                } else {
                    failureIndicator.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PageResponse> call, @NonNull Throwable t) {
                loadingIndicator.setValue(false);
                failureIndicator.setValue(null);
            }
        });

    }

    public MutableLiveData<Void> getFailureIndicator() {
        return failureIndicator;
    }

    public MutableLiveData<List<Movie>> getMovies() {
        return movies;
    }

    public MutableLiveData<Boolean> getLoadingIndicator() {
        return loadingIndicator;
    }

    private TheMovieDBApiService getTheMovieDBApiService() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://api.themoviedb.org/3/movie/")
                .client(okHttpClient)
                .build();
        return retrofit.create(TheMovieDBApiService.class);
    }

    private boolean isLastPage() {
        return currentPage >= totalPage;
    }

    public void movieListScrolled(int visibleItemCount, int lastVisiblePosition, int totalCount) {
        if ((visibleItemCount + lastVisiblePosition + SCROLL_THRESHOLD) >= totalCount && !isLastPage()) {
            getNextPage();
        }
    }
}
