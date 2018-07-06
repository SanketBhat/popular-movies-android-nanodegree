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

import android.arch.persistence.room.Room;
import android.content.Context;

import com.udacity.sanketbhat.popularmovies.api.TheMovieDBApiService;
import com.udacity.sanketbhat.popularmovies.database.MovieDao;
import com.udacity.sanketbhat.popularmovies.database.MovieDatabase;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class Dependencies {

    //Connection timeouts for network calls
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 15000;
    private static TheMovieDBApiService theMovieDBApiService = null;
    private static MovieDao movieDao = null;
    private static final String DATABASE_NAME = "movies.db";

    //Build retrofit library components and obtain service object
    public static synchronized TheMovieDBApiService getTheMovieDBApiService() {
        if (theMovieDBApiService == null) {
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
            theMovieDBApiService = retrofit.create(TheMovieDBApiService.class);
        }
        return theMovieDBApiService;
    }

    public static synchronized MovieDao getMovieDao(Context context) {
        if (movieDao == null) {
            //Use application context
            movieDao = Room.databaseBuilder(context.getApplicationContext(), MovieDatabase.class, DATABASE_NAME)
                    .build()
                    .getMovieDao();
        }
        return movieDao;
    }

}
