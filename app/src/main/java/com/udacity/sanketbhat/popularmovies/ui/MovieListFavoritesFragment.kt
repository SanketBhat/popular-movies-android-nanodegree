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
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.udacity.sanketbhat.popularmovies.R
import com.udacity.sanketbhat.popularmovies.adapter.MovieClickListener
import com.udacity.sanketbhat.popularmovies.adapter.MovieGridLayoutManager
import com.udacity.sanketbhat.popularmovies.adapter.MovieListAdapter
import com.udacity.sanketbhat.popularmovies.databinding.ActivityMovieListFavoritesBinding
import com.udacity.sanketbhat.popularmovies.model.Movie

class MovieListFavoritesFragment : Fragment() {
    private lateinit var viewModel: MovieListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            viewModel = ViewModelProvider(it).get(MovieListViewModel::class.java)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (activity != null) activity!!.title = "Favorites"
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.activity_movie_list_favorites, container, false)
        val mBinding: ActivityMovieListFavoritesBinding? = DataBindingUtil.bind(rootView)
        if (mBinding != null) {
            val adapter = MovieListAdapter(context!!, null, activity as MovieClickListener?)
            val gridLayoutManager = MovieGridLayoutManager(context!!, adapter)
            mBinding.favoritesMovieList.layoutManager = gridLayoutManager
            mBinding.favoritesMovieList.adapter = adapter
            viewModel.favorites?.observe(viewLifecycleOwner, { movies: List<Movie>? ->
                adapter.swapMovies(movies)
                if (movies != null && movies.isNotEmpty()) mBinding.emptyFavoritesTextView.visibility = View.GONE else mBinding.emptyFavoritesTextView.visibility = View.VISIBLE
            })
        }
        return rootView
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        var item = menu.findItem(R.id.menu_action_favorites)
        if (item != null) item.isVisible = false
        item = menu.findItem(R.id.menu_action_sort)
        if (item != null) item.isVisible = false
    }
}