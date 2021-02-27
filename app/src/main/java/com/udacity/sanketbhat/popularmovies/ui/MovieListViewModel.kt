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
package com.udacity.sanketbhat.popularmovies.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.udacity.sanketbhat.popularmovies.MovieRepository.Companion.getInstance
import com.udacity.sanketbhat.popularmovies.model.Movie
import com.udacity.sanketbhat.popularmovies.model.SortOrder
import kotlinx.coroutines.flow.Flow

//Accessed by android library
class MovieListViewModel(application: Application?) : AndroidViewModel(application!!) {

    private var repo = getInstance(getApplication<Application>().applicationContext)
    private var currentSortOrder: String? = null
    private var currentMovieResult: Flow<PagingData<Movie>>? = null
    private var favoriteMovieList: Flow<PagingData<Movie>>? = null

    fun getAllFavorites(): Flow<PagingData<Movie>> {
        favoriteMovieList?.let {
            return it
        }
        val newList = repo.getAllFavorites().cachedIn(viewModelScope)
        favoriteMovieList = newList
        return newList
    }

    fun getPagedMovies(sortOrder: Int): Flow<PagingData<Movie>> {
        val newSortOrder = SortOrder.getSortOrderPath(sortOrder)
        val lastResult = currentMovieResult
        if (newSortOrder == currentSortOrder && lastResult != null) {
            return lastResult
        }
        currentSortOrder = newSortOrder
        val newResult = repo.getPagedMovies(newSortOrder).cachedIn(viewModelScope)
        currentMovieResult = newResult
        return newResult
    }
}