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
package com.udacity.sanketbhat.popularmovies.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.sanketbhat.popularmovies.MovieRepository
import com.udacity.sanketbhat.popularmovies.MovieRepository.Companion.getInstance
import com.udacity.sanketbhat.popularmovies.MovieRepository.ReviewResponseCallback
import com.udacity.sanketbhat.popularmovies.MovieRepository.VideoResponseCallback
import com.udacity.sanketbhat.popularmovies.model.Movie
import com.udacity.sanketbhat.popularmovies.model.ReviewResponse
import com.udacity.sanketbhat.popularmovies.model.VideoResponse

//Accessed by android library
class MovieDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val reviewResponseLiveData: MutableLiveData<ReviewResponse?> = MutableLiveData()
    private val videoResponseLiveData: MutableLiveData<VideoResponse?> = MutableLiveData()

    //cached id for retrieving review, videos after configuration changes ex: rotation
    private var currentId = 0
    private val repo: MovieRepository? = getInstance(application.applicationContext)
    fun isFavorite(id: Int): LiveData<Movie> {
        return repo!!.isFavorite(id)
    }

    fun getReviewResponse(id: Int): MutableLiveData<ReviewResponse?> {
        if (id == currentId && reviewResponseLiveData.value != null) {
            //If cached value exist return
            return reviewResponseLiveData
        }
        currentId = id
        repo!!.getReviews(id, object : ReviewResponseCallback {
            override fun onReviewResponse(reviewResponse: ReviewResponse?) {
                reviewResponseLiveData.postValue(reviewResponse)
            }
        })
        return reviewResponseLiveData
    }

    fun getVideoResponse(id: Int): MutableLiveData<VideoResponse?> {
        if (id == currentId && videoResponseLiveData.value != null) {
            //If cached, return
            return videoResponseLiveData
        }
        currentId = id
        repo!!.getVideos(id, object : VideoResponseCallback {
            override fun onVideoResponse(videoResponse: VideoResponse?) {
                videoResponseLiveData.postValue(videoResponse)
            }
        })
        return videoResponseLiveData
    }

    fun insertIntoFavorites(movie: Movie?) {
        repo!!.insertMovie(movie)
    }

    fun removeFromFavorites(movie: Movie?) {
        repo!!.deleteMovie(movie)
    }

}