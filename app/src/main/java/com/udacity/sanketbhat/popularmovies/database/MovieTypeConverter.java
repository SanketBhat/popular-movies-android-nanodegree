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

package com.udacity.sanketbhat.popularmovies.database;


import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.udacity.sanketbhat.popularmovies.model.Genre;
import com.udacity.sanketbhat.popularmovies.model.ReviewResponse;
import com.udacity.sanketbhat.popularmovies.model.VideoResponse;

import java.util.List;

/**
 * Class that used by Room Library to save the unsupported types into the database.
 * The custom types such as Video, Review, Genre are converted to plain JSON String
 * to store it in the local database.
 */
class MovieTypeConverter {

    @TypeConverter
    public String genresToString(List<Genre> genres) {
        Gson gson = new Gson();
        return gson.toJson(genres);
    }

    @TypeConverter
    public List<Genre> stringToGenres(String genreString) {
        Gson gson = new Gson();
        return gson.fromJson(genreString, new TypeToken<List<Genre>>() {
        }.getType());
    }

    @TypeConverter
    public String intArrayToString(int[] genreIds) {
        StringBuilder stringBuilder = new StringBuilder("");
        for (int id :
                genreIds) {
            stringBuilder.append(id).append(",");
        }
        return stringBuilder.toString();
    }

    @TypeConverter
    public int[] stringToIntArray(String s) {
        String[] strings = s.split(",");
        int[] ids = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            try {
                ids[i] = Integer.parseInt(strings[i]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return ids;
    }

    @TypeConverter
    public String videosToString(VideoResponse videoResponse) {
        Gson gson = new Gson();
        return gson.toJson(videoResponse);
    }

    @TypeConverter
    public VideoResponse stringToVideos(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, VideoResponse.class);
    }

    @TypeConverter
    public String reviewsToString(ReviewResponse reviewResponse) {
        Gson gson = new Gson();
        return gson.toJson(reviewResponse);
    }

    @TypeConverter
    public ReviewResponse stringToReviews(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, ReviewResponse.class);
    }
}
