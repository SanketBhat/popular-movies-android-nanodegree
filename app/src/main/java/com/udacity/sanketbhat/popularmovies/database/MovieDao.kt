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

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.sanketbhat.popularmovies.model.Movie
import com.udacity.sanketbhat.popularmovies.model.ReviewResponse
import com.udacity.sanketbhat.popularmovies.model.VideoResponse

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: Movie?)

    @Delete
    fun delete(movie: Movie?)

    @Query("SELECT * FROM favorites WHERE id = :id")
    fun isFavorite(id: Int): LiveData<Movie>

    @Query("SELECT * FROM favorites WHERE id = :id")
    fun getMovie(id: Int): Movie?

    @get:Query("SELECT * FROM favorites ORDER BY title ASC")
    val allMovies: LiveData<List<Movie>>

    @Query("UPDATE favorites SET videoResponse = :videoResponse WHERE id = :id")
    fun updateVideos(id: Int, videoResponse: VideoResponse?)

    @Query("UPDATE favorites SET reviewResponse = :reviewResponse WHERE id = :id")
    fun updateReviews(id: Int, reviewResponse: ReviewResponse?)
}