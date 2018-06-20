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

package com.udacity.sanketbhat.popularmovies.model;

import com.google.gson.annotations.SerializedName;

class Genre {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String genreName;

    //Static data for genre
    public static String getGenreString(int genreId) {
        switch (genreId) {
            case 12:
                return "Adventure";
            case 14:
                return "Fantasy";
            case 16:
                return "Animation";
            case 18:
                return "Drama";
            case 27:
                return "Horror";
            case 28:
                return "Action";
            case 35:
                return "Comedy";
            case 36:
                return "History";
            case 37:
                return "Western";
            case 80:
                return "Crime";
            case 99:
                return "Documentary";
            case 878:
                return "Science Fiction";
            case 9648:
                return "Mystery";
            case 10402:
                return "Music";
            case 10749:
                return "Romance";
            case 10751:
                return "Family";
            case 10752:
                return "War";
            case 10770:
                return "TV Movie";
            case 53:
                return "Thriller";
            default:
                return "Unknown";
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}
