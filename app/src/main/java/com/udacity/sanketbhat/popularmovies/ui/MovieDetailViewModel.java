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
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.udacity.sanketbhat.popularmovies.MovieRepository;
import com.udacity.sanketbhat.popularmovies.model.Movie;
import com.udacity.sanketbhat.popularmovies.model.ReviewResponse;
import com.udacity.sanketbhat.popularmovies.model.VideoResponse;

@SuppressWarnings("WeakerAccess") //Accessed by android library
public class MovieDetailViewModel extends AndroidViewModel {
    private final MutableLiveData<ReviewResponse> reviewResponseLiveData;
    private final MutableLiveData<VideoResponse> videoResponseLiveData;
    //cached id for retrieving review, videos after configuration changes ex: rotation
    private int currentId;
    private final MovieRepository repo;

    public MovieDetailViewModel(Application application) {
        super(application);
        reviewResponseLiveData = new MutableLiveData<>();
        videoResponseLiveData = new MutableLiveData<>();
        repo = MovieRepository.getInstance(application.getApplicationContext());
    }

    public LiveData<Movie> isFavorite(int id) {
        return repo.isFavorite(id);
    }

    public MutableLiveData<ReviewResponse> getReviewResponse(int id) {
        if (id == currentId && reviewResponseLiveData.getValue() != null) {
            //If cached value exist return
            return reviewResponseLiveData;
        }
        currentId = id;
        repo.getReviews(id, reviewResponseLiveData::postValue);
        return reviewResponseLiveData;
    }

    public MutableLiveData<VideoResponse> getVideoResponse(int id) {
        if (id == currentId && videoResponseLiveData.getValue() != null) {
            //If cached, return
            return videoResponseLiveData;
        }
        currentId = id;
        repo.getVideos(id, videoResponseLiveData::postValue);
        return videoResponseLiveData;
    }

    public void insertIntoFavorites(Movie movie) {
        repo.insertMovie(movie);
    }

    public void removeFromFavorites(Movie movie) {
        repo.deleteMovie(movie);
    }
}
