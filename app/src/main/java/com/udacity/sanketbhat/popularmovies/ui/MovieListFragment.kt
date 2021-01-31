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

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.udacity.sanketbhat.popularmovies.R
import com.udacity.sanketbhat.popularmovies.adapter.MovieClickListener
import com.udacity.sanketbhat.popularmovies.adapter.MovieGridLayoutManager
import com.udacity.sanketbhat.popularmovies.adapter.MovieListAdapter
import com.udacity.sanketbhat.popularmovies.databinding.ActivityMovieListFragmentBinding
import com.udacity.sanketbhat.popularmovies.model.Movie
import com.udacity.sanketbhat.popularmovies.model.SortOrder
import com.udacity.sanketbhat.popularmovies.util.PreferenceUtils.getPreferredSortOrder

class MovieListFragment : Fragment() {
    private lateinit var viewModel: MovieListViewModel
    private var mBinding: ActivityMovieListFragmentBinding? = null
    private var gridLayoutManager: MovieGridLayoutManager? = null
    private var adapter: MovieListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MovieListViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setActivityTitle()
        val rootView = inflater.inflate(R.layout.activity_movie_list_fragment, container, false)
        mBinding = DataBindingUtil.bind(rootView)
        if (mBinding != null) {
            setupRecyclerView(mBinding!!.movieList)
            mBinding!!.movieListErrorRetryButton.setOnClickListener { loadFirstPage(true) }
            loadFirstPage(false)
            observeViewModel()
        }
        return rootView
    }

    private fun setActivityTitle() {
        if (activity != null) {
            when (getPreferredSortOrder(activity!!.applicationContext)) {
                SortOrder.SORT_ORDER_POPULAR -> activity!!.title = getString(R.string.title_popular_movie)
                SortOrder.SORT_ORDER_TOP_RATED -> activity!!.title = getString(R.string.title_top_rated_movie)
                else -> activity!!.setTitle(R.string.app_name)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort_popularity, R.id.menu_sort_rating -> {
                gridLayoutManager!!.scrollToPosition(0)
                adapter!!.swapMovies(null)
                loadFirstPage(false)
                setActivityTitle()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        //Initialize adapter with null movie items. Because it will be set once it is available
        adapter = MovieListAdapter(context!!, null, activity as MovieClickListener?)
        recyclerView.adapter = adapter
        gridLayoutManager = MovieGridLayoutManager(context!!, adapter!!)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)

        //Scroll listener for loading next page once user reach end of the page.
        recyclerView.addOnScrollListener(MovieListScrollListener())
    }

    private fun loadFirstPage(retrying: Boolean) {
        //Loading first page with the loading layout
        showMovieLoading()
        viewModel.getFirstPage(getPreferredSortOrder(context), retrying)
    }

    private fun observeViewModel() {
        //Observe loading indicator so that adapter can display loading for next page.
        viewModel.loadingIndicator?.observe(viewLifecycleOwner, { loading: Boolean? -> if (loading != null) adapter!!.setLoading(loading) })

        //Failure indicator for showing error message that user can understand
        viewModel.failureIndicator?.observe(viewLifecycleOwner, {
            if (adapter!!.isLoading()) {
                //Error when loading nextPage
                Log.i("MovieListActivity", "Error when loading next page")
            } else if (adapter!!.itemCount == 0) {
                showErrorLayout()
            }
        })

        //Whenever the movie list changes it notifies the adapter
        viewModel.movies?.observe(viewLifecycleOwner, { movies: List<Movie>? ->
            showMovieList()
            adapter!!.swapMovies(movies)
        })
    }

    //Helper methods for showing three different layouts list, loading, error.
    private fun showMovieList() {
        mBinding!!.movieList.visibility = View.VISIBLE
        mBinding!!.movieLoadProgress.visibility = View.GONE
        mBinding!!.movieListErrorLayout.visibility = View.GONE
    }

    private fun showMovieLoading() {
        mBinding!!.movieList.visibility = View.GONE
        mBinding!!.movieLoadProgress.visibility = View.VISIBLE
        mBinding!!.movieListErrorLayout.visibility = View.GONE
    }

    private fun showErrorLayout() {
        mBinding!!.movieList.visibility = View.GONE
        mBinding!!.movieLoadProgress.visibility = View.GONE
        mBinding!!.movieListErrorLayout.visibility = View.VISIBLE
    }

    //Custom scroll listener for endless list implementation
    internal inner class MovieListScrollListener : RecyclerView.OnScrollListener() {
        private val handler = Handler(Looper.getMainLooper())
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val itemCount = gridLayoutManager!!.itemCount
            val visibleItemCount = gridLayoutManager!!.childCount
            val lastVisiblePosition = gridLayoutManager!!.findLastVisibleItemPosition()
            if (visibleItemCount + lastVisiblePosition + 4 >= itemCount && !viewModel.isLastPage) {
                handler.post { viewModel.nextPage }
            }
        }
    }
}