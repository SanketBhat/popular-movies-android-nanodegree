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
package com.udacity.sanketbhat.popularmovies.model

import com.google.gson.annotations.SerializedName

//Some methods are used by retrofit API
//Now its using the static genre names because its not frequently changing
// can also download it dynamically
class Genre {
    @SerializedName("id")
    var id = 0

    @SerializedName("name")
    var genreName: String? = null

    companion object {
        //Static data for genre
        fun getGenreString(genreId: Int): String {
            return when (genreId) {
                12 -> "Adventure"
                14 -> "Fantasy"
                16 -> "Animation"
                18 -> "Drama"
                27 -> "Horror"
                28 -> "Action"
                35 -> "Comedy"
                36 -> "History"
                37 -> "Western"
                80 -> "Crime"
                99 -> "Documentary"
                878 -> "Science Fiction"
                9648 -> "Mystery"
                10402 -> "Music"
                10749 -> "Romance"
                10751 -> "Family"
                10752 -> "War"
                10770 -> "TV Movie"
                53 -> "Thriller"
                else -> "Unknown"
            }
        }
    }
}