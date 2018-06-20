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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailViewModel extends AndroidViewModel {
    private TheMovieDBApiService theMovieDBApiService;
    private MovieDao movieDao;
    private MutableLiveData<Movie> movie;

    public MovieDetailViewModel(Application application) {
        super(application);
        theMovieDBApiService = Dependencies.getTheMovieDBApiService();
        movieDao = Dependencies.getMovieDao(getApplication().getApplicationContext());
        movie = new MutableLiveData<>();
    }

    public LiveData<Movie> isFavorite(int id) {
        return movieDao.isFavorite(id);
    }

    public MutableLiveData<Movie> getMovie(int id) {
        theMovieDBApiService.getMovie(id).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Movie movieResult = response.body();
                    movie.setValue(movieResult);
                    Log.e("Data loaded", "Data Loaded");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {

            }
        });
        return movie;
    }
}
