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
package com.udacity.sanketbhat.popularmovies.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.udacity.sanketbhat.popularmovies.model.Genre
import com.udacity.sanketbhat.popularmovies.model.ReviewResponse
import com.udacity.sanketbhat.popularmovies.model.VideoResponse

/**
 * Class that used by Room Library to save the unsupported types into the database.
 * The custom types such as Video, Review, Genre are converted to plain JSON String
 * to store it in the local database.
 */
internal class MovieTypeConverter {
    @TypeConverter
    fun genresToString(genres: List<Genre>): String {
        val gson = Gson()
        return gson.toJson(genres)
    }

    @TypeConverter
    fun stringToGenres(genreString: String): List<Genre> {
        val gson = Gson()
        return gson.fromJson(genreString, object : TypeToken<List<Genre>>() {}.type)
    }

    @TypeConverter
    fun intArrayToString(genreIds: IntArray): String {
        val stringBuilder = StringBuilder("")
        for (id in genreIds) {
            stringBuilder.append(id).append(",")
        }
        return stringBuilder.toString()
    }

    @TypeConverter
    fun stringToIntArray(s: String): IntArray {
        val strings = s.split(",".toRegex()).toTypedArray()
        val ids = IntArray(strings.size)
        for (i in strings.indices) {
            try {
                ids[i] = strings[i].toInt()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
        }
        return ids
    }

    @TypeConverter
    fun videosToString(videoResponse: VideoResponse?): String {
        val gson = Gson()
        return gson.toJson(videoResponse)
    }

    @TypeConverter
    fun stringToVideos(jsonString: String?): VideoResponse {
        val gson = Gson()
        return gson.fromJson(jsonString, VideoResponse::class.java)
    }

    @TypeConverter
    fun reviewsToString(reviewResponse: ReviewResponse?): String {
        val gson = Gson()
        return gson.toJson(reviewResponse)
    }

    @TypeConverter
    fun stringToReviews(jsonString: String?): ReviewResponse {
        val gson = Gson()
        return gson.fromJson(jsonString, ReviewResponse::class.java)
    }
}