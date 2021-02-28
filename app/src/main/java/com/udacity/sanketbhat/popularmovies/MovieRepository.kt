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
package com.udacity.sanketbhat.popularmovies

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.udacity.sanketbhat.popularmovies.api.TheMovieDBApiService
import com.udacity.sanketbhat.popularmovies.database.MovieDao
import com.udacity.sanketbhat.popularmovies.model.Movie
import com.udacity.sanketbhat.popularmovies.model.ReviewResponse
import com.udacity.sanketbhat.popularmovies.model.VideoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor
import java.util.concurrent.Executors

//Class that manages data from network as well as local database
class MovieRepository private constructor(context: Context) {
    private val movieDao: MovieDao
    private val service: TheMovieDBApiService
    private val executor: Executor

    init {
        executor = Executors.newSingleThreadExecutor()
        movieDao = Dependencies.getMovieDao(context)
        service = Dependencies.theMovieDBApiService
    }

    fun getPagedMovies(sortOrder: String) = Pager(
            PagingConfig(MOVIE_PAGE_SIZE, enablePlaceholders = false, initialLoadSize = 20)
    ) {
        MoviePagingSource(service, sortOrder)
    }.flow

    fun getAllFavorites() = Pager(
            config = PagingConfig(pageSize = MOVIE_PAGE_SIZE, enablePlaceholders = false)
    ) {
        movieDao.allMovies()
    }.flow

    fun isFavorite(id: Int): LiveData<Movie> {
        return movieDao.isFavorite(id)
    }

    fun deleteMovie(movie: Movie?) {
        executor.execute { movieDao.delete(movie) }
    }

    fun insertMovie(movie: Movie?) {
        executor.execute { movieDao.insert(movie) }
    }

    fun getReviews(id: Int, callback: ReviewResponseCallback) {
        executor.execute {
            val movieFromDatabase = movieDao.getMovie(id)
            if (movieFromDatabase == null) loadReviewsFromInternet(id, false, callback) else if (movieFromDatabase.reviewResponse == null) loadReviewsFromInternet(id, true, callback) else callback.onReviewResponse(movieFromDatabase.reviewResponse)
        }
    }

    fun getVideos(id: Int, callback: VideoResponseCallback) {
        executor.execute {
            val movieFromDatabase = movieDao.getMovie(id)
            if (movieFromDatabase == null) loadVideoFromInternet(id, false, callback) else if (movieFromDatabase.videoResponse == null) loadVideoFromInternet(id, true, callback) else callback.onVideoResponse(movieFromDatabase.videoResponse)
        }
    }

    private fun loadVideoFromInternet(id: Int, isFavorite: Boolean, callback: VideoResponseCallback) {
        service.getVideos(id)?.enqueue(object : Callback<VideoResponse?> {
            override fun onResponse(call: Call<VideoResponse?>, response: Response<VideoResponse?>) {
                if (response.isSuccessful) {
                    val videoResponse = response.body()
                    callback.onVideoResponse(videoResponse)
                    if (isFavorite && videoResponse != null) {
                        executor.execute { movieDao.updateVideos(id, videoResponse) }
                    }
                } else callback.onVideoResponse(null)
            }

            override fun onFailure(call: Call<VideoResponse?>, t: Throwable) {
                callback.onVideoResponse(null)
            }
        })
    }

    private fun loadReviewsFromInternet(id: Int, isFavorite: Boolean, callback: ReviewResponseCallback) {
        service.getReviews(id)?.enqueue(object : Callback<ReviewResponse?> {
            override fun onResponse(call: Call<ReviewResponse?>, response: Response<ReviewResponse?>) {
                if (response.isSuccessful) {
                    val reviewResponse = response.body()
                    callback.onReviewResponse(reviewResponse)
                    if (isFavorite && reviewResponse != null) {
                        executor.execute { movieDao.updateReviews(id, reviewResponse) }
                    }
                } else callback.onReviewResponse(null)
            }

            override fun onFailure(call: Call<ReviewResponse?>, t: Throwable) {
                callback.onReviewResponse(null)
            }
        })
    }

    interface ReviewResponseCallback {
        fun onReviewResponse(reviewResponse: ReviewResponse?)
    }

    interface VideoResponseCallback {
        fun onVideoResponse(videoResponse: VideoResponse?)
    }

    companion object {
        private val LOCK = Any()
        private var movieRepository: MovieRepository? = null

        //Singleton
        @JvmStatic
        fun getInstance(context: Context): MovieRepository {
            if (movieRepository == null) {
                synchronized(LOCK) { movieRepository = MovieRepository(context) }
            }
            return movieRepository!!
        }
    }
}