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
import androidx.room.Room
import com.udacity.sanketbhat.popularmovies.api.TheMovieDBApiService
import com.udacity.sanketbhat.popularmovies.database.MovieDao
import com.udacity.sanketbhat.popularmovies.database.MovieDatabase
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Gives the singletons of required classes.
 */
internal object Dependencies {
    //Connection timeouts for network calls
    private const val CONNECTION_TIMEOUT = 10000
    private const val READ_TIMEOUT = 15000

    private const val DATABASE_NAME = "movies.db"
    //Build retrofit library components and obtain service object

    val theMovieDBApiService: TheMovieDBApiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build()
        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.themoviedb.org/3/movie/")
                .client(okHttpClient)
                .build()
        retrofit.create(TheMovieDBApiService::class.java)
    }
    var movieDao: MovieDao? = null
    fun getMovieDao(context: Context): MovieDao =
            movieDao ?: synchronized(this) {
                movieDao ?: initializeMovieDao(context).also { movieDao = it }
            }

    private fun initializeMovieDao(context: Context): MovieDao {
        return Room.databaseBuilder(context.applicationContext, MovieDatabase::class.java, DATABASE_NAME)
                .build()
                .movieDao!!
    }
}