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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.udacity.sanketbhat.popularmovies.MovieRepository.Companion.getInstance
import com.udacity.sanketbhat.popularmovies.model.Movie
import com.udacity.sanketbhat.popularmovies.model.SortOrder
import com.udacity.sanketbhat.popularmovies.ui.views.AppScreen
import kotlinx.coroutines.flow.Flow

//Accessed by android library
class MovieListViewModel(application: Application?) : AndroidViewModel(application!!) {

    private var repo = getInstance(getApplication<Application>().applicationContext)
    private val _currentScreen = MutableLiveData<AppScreen>()
    val currentScreen: LiveData<AppScreen> = _currentScreen
    private var currentPopularMovieResult: Flow<PagingData<Movie>>? = null
    private var currentTopRatedMovieResult: Flow<PagingData<Movie>>? = null
    private var favoriteMovieList: Flow<PagingData<Movie>>? = null
    var showCreditsDialog = MutableLiveData(false)

    fun setCurrentScreen(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun getAllFavorites(): Flow<PagingData<Movie>> {
        favoriteMovieList?.let {
            return it
        }
        val newList = repo.getAllFavorites().cachedIn(viewModelScope)
        favoriteMovieList = newList
        return newList
    }

    fun getPopularMovies(): Flow<PagingData<Movie>> {
        val lastResult = currentPopularMovieResult
        if (lastResult != null) {
            return lastResult
        }
        val newResult = repo.getPagedMovies(SortOrder.URL_PATH_POPULAR).cachedIn(viewModelScope)
        currentPopularMovieResult = newResult
        return newResult
    }

    fun getTopRatedMovies(): Flow<PagingData<Movie>> {
        val lastResult = currentTopRatedMovieResult
        if (lastResult != null) {
            return lastResult
        }
        val newResult = repo.getPagedMovies(SortOrder.URL_PATH_TOP_RATED).cachedIn(viewModelScope)
        currentTopRatedMovieResult = newResult
        return newResult
    }
}