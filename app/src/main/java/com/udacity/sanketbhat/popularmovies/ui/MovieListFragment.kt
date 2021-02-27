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
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.sanketbhat.popularmovies.R
import com.udacity.sanketbhat.popularmovies.adapter.MovieClickListener
import com.udacity.sanketbhat.popularmovies.adapter.MovieGridLayoutManager
import com.udacity.sanketbhat.popularmovies.adapter.MovieListAdapter
import com.udacity.sanketbhat.popularmovies.adapter.MovieLoadStateAdapter
import com.udacity.sanketbhat.popularmovies.databinding.ActivityMovieListFragmentBinding
import com.udacity.sanketbhat.popularmovies.model.SortOrder
import com.udacity.sanketbhat.popularmovies.util.PreferenceUtils.getPreferredSortOrder
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class MovieListFragment : Fragment() {
    private lateinit var viewModel: MovieListViewModel
    private var mBinding: ActivityMovieListFragmentBinding? = null
    private var gridLayoutManager: MovieGridLayoutManager? = null
    private var adapter: MovieListAdapter? = null
    private var movieRequestJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MovieListViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setActivityTitle()
        val rootView = inflater.inflate(R.layout.activity_movie_list_fragment, container, false)
        mBinding = DataBindingUtil.bind(rootView)
        mBinding?.let { mBinding ->
            showMovieList()
            setupRecyclerView(mBinding.movieList)
            mBinding.movieListErrorRetryButton.setOnClickListener { adapter?.refresh() }
            requestForMovies()
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

    private fun requestForMovies() {
        movieRequestJob?.cancel()
        movieRequestJob = lifecycleScope.launch {
            viewModel.getPagedMovies(getPreferredSortOrder(context)).collectLatest {
                adapter?.submitData(it)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort_popularity, R.id.menu_sort_rating -> {
                adapter?.submitData(lifecycle, PagingData.empty())
                requestForMovies()
                setActivityTitle()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        //Initialize adapter with null movie items. Because it will be set once it is available
        adapter = MovieListAdapter(context!!, activity as MovieClickListener?)

        adapter?.addLoadStateListener { loadState ->
            when (loadState.source.refresh) {
                is LoadState.NotLoading -> showMovieList()
                is LoadState.Loading -> showMovieLoading()
                is LoadState.Error -> showErrorLayout()
            }
        }
        lifecycleScope.launch {
            adapter?.loadStateFlow
                    // Only emit when REFRESH LoadState for RemoteMediator changes.
                    ?.distinctUntilChangedBy { it.refresh }
                    // Only react to cases where Remote REFRESH completes i.e., NotLoading.
                    ?.filter { it.refresh is LoadState.NotLoading }
                    ?.collect { mBinding?.movieList?.scrollToPosition(0) }
        }

        val footerAdapter = MovieLoadStateAdapter{adapter?.retry()}
        adapter?.addLoadStateListener { loadState -> footerAdapter.loadState = loadState.append }
        val concatAdapter = ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build(),adapter, footerAdapter);
        recyclerView.adapter = concatAdapter
        gridLayoutManager = MovieGridLayoutManager(context!!, recyclerView.adapter!!)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
    }

    //Helper methods for showing three different layouts list, loading, error.
    private fun showMovieList() {
        mBinding?.movieList?.visibility = View.VISIBLE
        mBinding?.movieLoadProgress?.visibility = View.GONE
        mBinding?.movieListErrorLayout?.visibility = View.GONE
    }

    private fun showMovieLoading() {
        mBinding?.movieList?.visibility = View.GONE
        mBinding?.movieLoadProgress?.visibility = View.VISIBLE
        mBinding?.movieListErrorLayout?.visibility = View.GONE
    }

    private fun showErrorLayout() {
        mBinding?.movieList?.visibility = View.GONE
        mBinding?.movieLoadProgress?.visibility = View.GONE
        mBinding?.movieListErrorLayout?.visibility = View.VISIBLE
    }
}