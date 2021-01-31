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

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.squareup.picasso.Picasso
import com.udacity.sanketbhat.popularmovies.R
import com.udacity.sanketbhat.popularmovies.adapter.MovieClickListener
import com.udacity.sanketbhat.popularmovies.model.*
import com.udacity.sanketbhat.popularmovies.util.PreferenceUtils.getPreferredSortOrder
import com.udacity.sanketbhat.popularmovies.util.PreferenceUtils.setPreferredSortOrder

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [MovieDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class MovieListActivity : AppCompatActivity(), MovieClickListener, FragmentManager.OnBackStackChangedListener {
    private var mTwoPane = false
    private var mOptionMenu: Menu? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        //Prepare toolbar and set title
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title
        if (findViewById<View?>(R.id.movie_detail_container) != null) {
            //the detail fragment shown only in the larger displays.
            mTwoPane = true
        }
        supportFragmentManager.addOnBackStackChangedListener(this)
        if (savedInstanceState == null) {
            val fragment = MovieListFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.movie_list_container, fragment)
                    .commit()
        }
        onBackStackChanged()
    }

    override fun onStop() {
        //Cancel all requests created within adapter.
        Picasso.with(this)
                .cancelTag(MovieListActivity::class.java.simpleName)
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        mOptionMenu = menu
        setupOptionMenu()
        return true
    }

    private fun setupOptionMenu() {
        when (getPreferredSortOrder(this)) {
            SortOrder.SORT_ORDER_POPULAR -> mOptionMenu!!.findItem(R.id.menu_sort_popularity).isChecked = true
            SortOrder.SORT_ORDER_TOP_RATED -> mOptionMenu!!.findItem(R.id.menu_sort_rating).isChecked = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort_popularity -> {
                item.isChecked = true
                return changeSortOrderTo(SortOrder.SORT_ORDER_POPULAR)
            }
            R.id.menu_sort_rating -> {
                item.isChecked = true
                return changeSortOrderTo(SortOrder.SORT_ORDER_TOP_RATED)
            }
            R.id.menu_action_favorites -> {
                val movieListFavoritesFragment = MovieListFavoritesFragment()
                supportFragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.movie_list_container, movieListFavoritesFragment)
                        .addToBackStack(null)
                        .commit()
            }
            R.id.menu_main_credits -> {
                val dialog = AlertDialog.Builder(this)
                        .setTitle(R.string.menu_main_credits)
                        .setMessage(R.string.credits_string)
                        .setPositiveButton(R.string.credit_dialog_button) { dialog1: DialogInterface, _: Int -> dialog1.dismiss() }.create()
                dialog.show()
            }
            android.R.id.home -> if (supportFragmentManager.backStackEntryCount > 0) supportFragmentManager.popBackStack()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeSortOrderTo(sortOrder: Int): Boolean {
        return if (getPreferredSortOrder(this) != sortOrder) {
            setPreferredSortOrder(this, sortOrder)
            false
        } else {
            true
        }
    }

    override fun onClickItem(v: View?, movie: Movie?) {
        //Pass the clicked movie item.
        //It will be removed in stage 2.
        val arguments = Bundle()
        arguments.putParcelable(MovieDetailFragment.ARG_ITEM, movie)

        //If twoPane layout just inflate/ replace the fragment at the right side.
        if (mTwoPane) {
            val fragment = MovieDetailFragment()
            fragment.arguments = arguments
            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.movie_detail_container, fragment)
                    .commit()
        } else {
            //Prepare items for shared element transactions
            val imageView = v!!.findViewById<ImageView>(R.id.movie_grid_item_image)
            val transition = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this, imageView, getString(R.string.shared_element_transition_name))
                    .toBundle()

            //If not twoPane layout start the MovieDetailActivity
            val intent = Intent(this, MovieDetailActivity::class.java)
            intent.putExtras(arguments)
            startActivity(intent, transition)
        }
    }

    override fun onBackStackChanged() {
        val backStackEntryCount = supportFragmentManager.backStackEntryCount
        val actionBar = supportActionBar
        if (actionBar != null) {
            if (backStackEntryCount <= 0) {
                actionBar.setDisplayHomeAsUpEnabled(false)
            } else {
                actionBar.setDisplayHomeAsUpEnabled(true)
            }
        }
    }
}