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

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat

@Entity(tableName = "favorites")
class Movie : Parcelable {
    @PrimaryKey
    @SerializedName("id")
    var id = 0

    @SerializedName("title")
    var title: String? = null

    @SerializedName("vote_average")
    var voteAverage = 0.0

    //Used by room library implicitly
    @SerializedName("release_date")
    var releaseDate: String? = null

    @SerializedName("genre_ids")
    var genreIds: IntArray? = null

    @SerializedName("backdrop_path")
    var backdropPath: String? = null

    @SerializedName("overview")
    var overview: String? = null

    @SerializedName("poster_path")
    var posterPath: String? = null

    @SerializedName("adult")
    var adult = false

    @SerializedName("videos")
    var videoResponse: VideoResponse? = null

    @SerializedName("genres")
    var genres: List<Genre>? = null

    @SerializedName("reviews")
    var reviewResponse: ReviewResponse? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        id = `in`.readInt()
        title = `in`.readString()
        voteAverage = `in`.readDouble()
        releaseDate = `in`.readString()
        genreIds = `in`.createIntArray()
        backdropPath = `in`.readString()
        overview = `in`.readString()
        posterPath = `in`.readString()
        adult = `in`.readByte().toInt() != 0
    }//If Genre list is not null, return its names

    /**
     * This method returns the string using the Genre object list. If it's null then
     * it returns the string using the static data of the genre. [Genre]
     *
     * @return returns the joined Genres of the movie separated by (,) or empty string.
     */
    val genresString: String
        get() {
            //If Genre list is not null, return its names
            if (genres != null && genres!!.isNotEmpty()) {
                val strings = arrayOfNulls<String>(genres!!.size)
                for (i in genres!!.indices) {
                    strings[i] = genres!![i].genreName
                }
                return TextUtils.join(", ", strings)
            }
            if (genreIds == null){
                genres = listOf()
                return ""
            }else{
                val createdGenres: ArrayList<Genre> = arrayListOf()
                val strings = arrayOfNulls<String>(genreIds!!.size)
                for (i in genreIds!!.indices) {
                    strings[i] = Genre.getGenreString(genreIds!![i])
                    createdGenres.add(Genre().also { it.genreName })
                }
                return TextUtils.join(", ", strings)
            }

        }

    /**
     * Tries to convert the received date string to local date format. In case of any errors
     * returns the original date string.
     * @return date string on of (yyyy-MM-dd) or local date format.
     */
    val displayReleaseDate: String?
        get() {
            try {
                @SuppressLint("SimpleDateFormat") val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                val date = simpleDateFormat.parse(releaseDate)
                return SimpleDateFormat.getDateInstance().format(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return releaseDate
        }
    val videos: List<Video?>?
        get() = videoResponse?.videos

    //normal getter
    val reviews: List<Review?>?
        get() = reviewResponse?.reviews

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(title)
        dest.writeDouble(voteAverage)
        dest.writeString(releaseDate)
        dest.writeIntArray(genreIds)
        dest.writeString(backdropPath)
        dest.writeString(overview)
        dest.writeString(posterPath)
        dest.writeByte((if (adult) 1 else 0).toByte())
    }

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }

        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }
}