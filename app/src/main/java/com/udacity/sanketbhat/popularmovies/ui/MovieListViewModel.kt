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
import com.udacity.sanketbhat.popularmovies.MovieRepository
import com.udacity.sanketbhat.popularmovies.MovieRepository.Companion.getInstance
import com.udacity.sanketbhat.popularmovies.model.Movie
import com.udacity.sanketbhat.popularmovies.model.PageResponse
import com.udacity.sanketbhat.popularmovies.model.SortOrder.getSortOrderPath
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//Accessed by android library
class MovieListViewModel(application: Application?) : AndroidViewModel(application!!) {
    //page numbers for the correct pagination
    private var currentPage = 0
    private var totalPage = 0

    //Observable items from the activity
    var movies: MutableLiveData<ArrayList<Movie>?>? = null
        private set
    var failureIndicator: MutableLiveData<Void?>? = null
        private set
    var loadingIndicator: MutableLiveData<Boolean?>? = null
        private set
    var favorites: LiveData<List<Movie>>? = null
        private set
    private var sortOrder = 0
    private var repo: MovieRepository? = null

    //Initialize data/objects
    private fun initialize() {
        repo = getInstance(getApplication<Application>().applicationContext)
        movies = MutableLiveData()
        failureIndicator = MutableLiveData()
        loadingIndicator = MutableLiveData()
        loadingIndicator!!.value = false
        favorites = repo!!.favorites
    }

    /**
     * Request for the first page of the list, called from MainListActivity
     *
     * @param sortOrder the sort order to list the movies, also used to distinguish between
     * real calls and calls triggered by configuration changes
     * @param retrying  this is flag determines whether it is retrying. If true it loads movie
     * list even if it already exist
     */
    fun getFirstPage(sortOrder: Int, retrying: Boolean) {
        //It implies it is called by configuration changes, just return.
        if (this.sortOrder == sortOrder && !retrying) return

        //It is intentionally called, proceed with loading first page
        this.sortOrder = sortOrder
        val pageCallback: Callback<PageResponse?> = object : Callback<PageResponse?> {
            override fun onResponse(call: Call<PageResponse?>, response: Response<PageResponse?>) {
                if (response.isSuccessful) {
                    val pageResponse = response.body()
                    if (pageResponse != null) {
                        //If loading successful, update data and return
                        currentPage = pageResponse.page!!
                        totalPage = pageResponse.totalPages!!
                        movies!!.value = ArrayList(pageResponse.movies ?: emptyList())
                        return
                    }
                }
                failureIndicator!!.value = null
            }

            override fun onFailure(call: Call<PageResponse?>, t: Throwable) {
                //If call failed trigger failureIndicator so that observing activity shows
                //Error layout
                failureIndicator!!.value = null
            }
        }
        repo!!.getFirstPage(getSortOrderPath(sortOrder), pageCallback)
    }

    //Next page loaded, append it to existing list
    val nextPage: Unit
        get() {
            if (loadingIndicator!!.value != null && loadingIndicator!!.value!!) return
            loadingIndicator!!.value = true
            val pageCallback: Callback<PageResponse?> = object : Callback<PageResponse?> {
                override fun onResponse(call: Call<PageResponse?>, response: Response<PageResponse?>) {
                    loadingIndicator!!.value = false
                    if (response.isSuccessful) {
                        val pageResponse = response.body()
                        if (pageResponse != null && movies!!.value != null) {

                            //Next page loaded, append it to existing list
                            currentPage = pageResponse.page!!
                            totalPage = pageResponse.totalPages!!
                            pageResponse.movies?.let {
                                movies!!.value?.addAll(it)
                            }
                            return
                        }
                    }
                    failureIndicator!!.value = null
                }

                override fun onFailure(call: Call<PageResponse?>, t: Throwable) {
                    loadingIndicator!!.value = false
                    failureIndicator!!.value = null
                }
            }
            repo!!.getNextPage(getSortOrderPath(sortOrder), currentPage + 1, pageCallback)
        }
    val isLastPage: Boolean
        get() = currentPage >= totalPage

    init {
        initialize()
    }
}