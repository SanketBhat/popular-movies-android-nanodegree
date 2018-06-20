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

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.udacity.sanketbhat.popularmovies.Dependencies;
import com.udacity.sanketbhat.popularmovies.api.TheMovieDBApiService;
import com.udacity.sanketbhat.popularmovies.database.MovieDao;
import com.udacity.sanketbhat.popularmovies.model.Movie;
import com.udacity.sanketbhat.popularmovies.model.PageResponse;
import com.udacity.sanketbhat.popularmovies.model.SortOrder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListViewModel extends AndroidViewModel {

    //page numbers for the correct pagination
    private int currentPage = 0;
    private int totalPage = 0;

    //Observable items from the activity
    private MutableLiveData<List<Movie>> movies;
    private MutableLiveData<Void> failureIndicator;
    private MutableLiveData<Boolean> loadingIndicator;

    private int sortOrder;
    private TheMovieDBApiService service;
    private MovieDao movieDao;


    public MovieListViewModel(Application application) {
        super(application);
        initialize();
    }

    //Initialize data/objects
    private void initialize() {
        if (movies == null) movies = new MutableLiveData<>();

        if (service == null) service = Dependencies.getTheMovieDBApiService();

        if (failureIndicator == null) failureIndicator = new MutableLiveData<>();

        if (loadingIndicator == null) {
            loadingIndicator = new MutableLiveData<>();
            loadingIndicator.setValue(false);
        }

        movieDao = Dependencies.getMovieDao(getApplication().getApplicationContext());
    }

    /**
     * Request for the first page of the list, called from MainListActivity
     *
     * @param sortOrder the sort order to list the movies, also used to distinguish between
     *                  real calls and calls triggered by configuration changes
     * @param retrying  this is flag determines whether it is retrying. If true it loads movie
     *                  list even if it already exist
     */
    public void getFirstPage(int sortOrder, boolean retrying) {

        test();

        //It implies it is called by configuration changes, just return.
        if (this.sortOrder == sortOrder && !retrying) return;

        //It is intentionally called, proceed with loading first page
        this.sortOrder = sortOrder;
        Call<PageResponse> call = service.getFirstPage(SortOrder.getSortOrderPath(sortOrder));
        call.enqueue(new Callback<PageResponse>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse> call, @NonNull Response<PageResponse> response) {
                if (response.isSuccessful()) {
                    PageResponse pageResponse = response.body();
                    if (pageResponse != null) {
                        //If loading successful, update data and return
                        currentPage = pageResponse.getPage();
                        totalPage = pageResponse.getTotalPages();
                        movies.setValue(pageResponse.getMovies());
                        return;
                    }
                }
                failureIndicator.setValue(null);
            }

            @Override
            public void onFailure(@NonNull Call<PageResponse> call, @NonNull Throwable t) {
                //If call failed trigger failureIndicator so that observing activity shows
                //Error layout
                failureIndicator.setValue(null);
            }
        });
    }

    private void test() {
        service.getMovie(24428).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                Movie movie = response.body();
                Log.e("Loaded", "onResponse: Loaded");
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                Log.e("Loaded", "onResponse: Loaded");
            }
        });
    }

    public void getNextPage() {
        if (loadingIndicator.getValue() != null && loadingIndicator.getValue()) return;

        loadingIndicator.setValue(true);
        service.getPage(SortOrder.getSortOrderPath(sortOrder), currentPage + 1).enqueue(new Callback<PageResponse>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse> call, @NonNull Response<PageResponse> response) {
                loadingIndicator.setValue(false);
                if (response.isSuccessful()) {
                    PageResponse pageResponse = response.body();
                    if (pageResponse != null && movies.getValue() != null) {

                        //Next page loaded, append it to existing list
                        currentPage = pageResponse.getPage();
                        totalPage = pageResponse.getTotalPages();
                        movies.getValue().addAll(pageResponse.getMovies());
                        return;
                    }
                }
                failureIndicator.setValue(null);
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

    public LiveData<List<Movie>> getFavorites() {
        return movieDao.getAllMovies();
    }

    public boolean isLastPage() {
        return currentPage >= totalPage;
    }
}
