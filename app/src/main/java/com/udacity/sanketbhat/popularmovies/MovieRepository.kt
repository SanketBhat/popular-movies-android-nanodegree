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

import androidx.lifecycle.LiveData;
import android.content.Context;
import androidx.annotation.NonNull;

import com.udacity.sanketbhat.popularmovies.api.TheMovieDBApiService;
import com.udacity.sanketbhat.popularmovies.database.MovieDao;
import com.udacity.sanketbhat.popularmovies.model.Movie;
import com.udacity.sanketbhat.popularmovies.model.PageResponse;
import com.udacity.sanketbhat.popularmovies.model.ReviewResponse;
import com.udacity.sanketbhat.popularmovies.model.VideoResponse;

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

    //Singleton
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

    public void getReviews(int id, ReviewResponseCallback callback) {
        executor.execute(() -> {
            Movie movieFromDatabase = movieDao.getMovie(id);
            if (movieFromDatabase == null)
                loadReviewsFromInternet(id, false, callback);
            else if (movieFromDatabase.getReviewResponse() == null)
                loadReviewsFromInternet(id, true, callback);
            else callback.onReviewResponse(movieFromDatabase.getReviewResponse());
        });
    }

    public void getVideos(int id, VideoResponseCallback callback) {
        executor.execute(() -> {
            Movie movieFromDatabase = movieDao.getMovie(id);
            if (movieFromDatabase == null)
                loadVideoFromInternet(id, false, callback);
            else if (movieFromDatabase.getVideoResponse() == null)
                loadVideoFromInternet(id, true, callback);
            else callback.onVideoResponse(movieFromDatabase.getVideoResponse());
        });
    }

    private void loadVideoFromInternet(int id, boolean isFavorite, VideoResponseCallback callback) {
        service.getVideos(id).enqueue(new Callback<VideoResponse>() {

            @Override
            public void onResponse(@NonNull Call<VideoResponse> call, @NonNull Response<VideoResponse> response) {
                if (response.isSuccessful()) {
                    VideoResponse videoResponse = response.body();
                    callback.onVideoResponse(videoResponse);
                    if (isFavorite && videoResponse != null) {
                        executor.execute(() -> movieDao.updateVideos(id, videoResponse));
                    }
                } else callback.onVideoResponse(null);
            }

            @Override
            public void onFailure(@NonNull Call<VideoResponse> call, @NonNull Throwable t) {
                callback.onVideoResponse(null);
            }
        });

    }

    private void loadReviewsFromInternet(int id, boolean isFavorite, ReviewResponseCallback callback) {
        service.getReviews(id).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReviewResponse> call, @NonNull Response<ReviewResponse> response) {
                if (response.isSuccessful()) {
                    ReviewResponse reviewResponse = response.body();
                    callback.onReviewResponse(reviewResponse);
                    if (isFavorite && reviewResponse != null) {
                        executor.execute(() -> movieDao.updateReviews(id, reviewResponse));
                    }
                } else callback.onReviewResponse(null);
            }

            @Override
            public void onFailure(@NonNull Call<ReviewResponse> call, @NonNull Throwable t) {
                callback.onReviewResponse(null);
            }
        });
    }

    public interface ReviewResponseCallback {
        void onReviewResponse(ReviewResponse reviewResponse);
    }

    public interface VideoResponseCallback {
        void onVideoResponse(VideoResponse videoResponse);
    }
}
