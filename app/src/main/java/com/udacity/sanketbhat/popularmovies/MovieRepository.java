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

package com.udacity.sanketbhat.popularmovies;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.udacity.sanketbhat.popularmovies.api.TheMovieDBApiService;
import com.udacity.sanketbhat.popularmovies.database.MovieDao;
import com.udacity.sanketbhat.popularmovies.model.Movie;
import com.udacity.sanketbhat.popularmovies.model.PageResponse;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Class that manages data from network as well as local database
public class MovieRepository {

    private static final Object LOCK = new Object();
    private static MovieRepository movieRepository;
    private final MovieDao movieDao;
    private final TheMovieDBApiService service;
    private final Executor executor;

    private MovieRepository(Context context) {
        executor = Executors.newSingleThreadExecutor();
        movieDao = Dependencies.getMovieDao(context);
        service = Dependencies.getTheMovieDBApiService();
    }

    public static MovieRepository getInstance(Context context) {
        if (movieRepository == null) {
            synchronized (LOCK) {
                movieRepository = new MovieRepository(context);
            }
        }
        return movieRepository;
    }

    public void getNextPage(String sortOrder, int position, final Callback<PageResponse> responseCallback) {
        service.getPage(sortOrder, position).enqueue(responseCallback);
    }

    public void getFirstPage(String sortOrder, final Callback<PageResponse> responseCallBack) {
        service.getFirstPage(sortOrder).enqueue(responseCallBack);
    }

    public LiveData<List<Movie>> getFavorites() {
        return movieDao.getAllMovies();
    }

    public LiveData<Movie> isFavorite(int id) {
        return movieDao.isFavorite(id);
    }

    public void deleteMovie(final Movie movie) {
        executor.execute(() -> movieDao.delete(movie));
    }

    public void insertMovie(final Movie movie) {
        executor.execute(() -> movieDao.insert(movie));
    }

    public void getMovie(int id, ResponseCallback callback) {
        executor.execute(() -> {
            Movie movieFromDatabase = movieDao.getMovie(id);
            if (movieFromDatabase == null) loadFromInternet(id, false, callback);
            else if (movieFromDatabase.getReviewResponse() == null || movieFromDatabase.getVideoResponse() == null)
                loadFromInternet(id, true, callback);
            else callback.onResponse(movieFromDatabase);
        });
    }

    private void loadFromInternet(int id, boolean isFavorite, ResponseCallback callback) {
        service.getMovie(id).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Movie movieResult = response.body();
                    callback.onResponse(movieResult);
                    if (isFavorite && movieResult != null
                            && movieResult.getVideoResponse() != null
                            && movieResult.getReviewResponse() != null) {
                        movieDao.updateVideos(id, movieResult.getVideoResponse());
                        movieDao.updateReviews(id, movieResult.getReviewResponse());
                    }
                } else {
                    callback.onResponse(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                callback.onResponse(null);
            }
        });
    }

    public interface ResponseCallback {
        void onResponse(Movie movie);
    }
}
