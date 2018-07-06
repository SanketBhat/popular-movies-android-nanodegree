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

import com.udacity.sanketbhat.popularmovies.MovieRepository;
import com.udacity.sanketbhat.popularmovies.model.Movie;

class MovieDetailViewModel extends AndroidViewModel {
    private final MutableLiveData<Movie> movie;
    private final MovieRepository repo;

    public MovieDetailViewModel(Application application) {
        super(application);
        movie = new MutableLiveData<>();
        repo = MovieRepository.getInstance(application.getApplicationContext());
    }

    public LiveData<Movie> isFavorite(int id) {
        return repo.isFavorite(id);
    }

    public MutableLiveData<Movie> getMovie(int id) {
        if (movie.getValue() != null
                && movie.getValue().getId() == id
                && movie.getValue().getVideoResponse() != null
                && movie.getValue().getReviewResponse() != null) return movie;
        repo.getMovie(id, movie::postValue);
        return movie;
    }

    public void insertIntoFavorites(Movie movie) {
        repo.insertMovie(movie);
    }

    public void removeFromFavorites(Movie movie) {
        repo.deleteMovie(movie);
    }
}
