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

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModelProvider
import com.udacity.sanketbhat.popularmovies.R
import com.udacity.sanketbhat.popularmovies.model.Movie
import com.udacity.sanketbhat.popularmovies.ui.views.*

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [MovieDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class MovieListActivity : AppCompatActivity() {
    private var mTwoPane = false
    private lateinit var viewModel: MovieListViewModel

    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MovieListViewModel::class.java)
        mTwoPane = resources.getBoolean(R.bool.isTablet)
        val screens = listOf(
            AppScreen("Popular", Icons.Default.Star, viewModel::getPopularMovies),
            AppScreen("Top Rated", Icons.Default.ThumbUp, viewModel::getTopRatedMovies),
            AppScreen("Favorites", Icons.Default.Favorite, viewModel::getAllFavorites)
        )
        setContent {
            val currentScreen by viewModel.currentScreen.observeAsState(screens[0])
            val showCredits by viewModel.showCreditsDialog.observeAsState(initial = false)
            PopMoviesTheme {
                AppScaffold(currentScreen, screens, viewModel) {
                    if (mTwoPane) {
                        TwoPaneLayout(currentScreen, this::onClickItem)
                    } else {
                        SinglePaneLayout(currentScreen, this::onClickItem)
                    }
                    if (showCredits) {
                        CreditsDialog(onClose = { viewModel.showCreditsDialog.value = false })
                    }
                }
            }
        }
    }


    private fun onClickItem(movie: Movie) {
        //Pass the clicked movie item.
        //It will be removed in stage 2.
        val arguments = Bundle()
        arguments.putParcelable(MovieDetailFragment.ARG_ITEM, movie)

        //If twoPane layout just inflate/ replace the fragment at the right side.
        if (!mTwoPane) {
            //If not twoPane layout start the MovieDetailActivity
            val intent = Intent(this, MovieDetailActivity::class.java)
            intent.putExtras(arguments)
            startActivity(intent)
        }
    }
}