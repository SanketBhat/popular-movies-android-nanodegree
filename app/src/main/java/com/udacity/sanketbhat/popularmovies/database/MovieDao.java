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

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.udacity.sanketbhat.popularmovies.model.Movie;
import com.udacity.sanketbhat.popularmovies.model.ReviewResponse;
import com.udacity.sanketbhat.popularmovies.model.VideoResponse;

import java.util.List;

@Dao
public interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Movie movie);

    @Delete
    void delete(Movie movie);

    @Query("SELECT * FROM favorites WHERE id = :id")
    LiveData<Movie> isFavorite(int id);

    @Query("SELECT * FROM favorites WHERE id = :id")
    Movie getMovie(int id);

    @Query("SELECT * FROM favorites ORDER BY title ASC")
    LiveData<List<Movie>> getAllMovies();

    @Query("UPDATE favorites SET videoResponse = :videoResponse WHERE id = :id")
    void updateVideos(int id, VideoResponse videoResponse);

    @Query("UPDATE favorites SET reviewResponse = :reviewResponse WHERE id = :id")
    void updateReviews(int id, ReviewResponse reviewResponse);
}
