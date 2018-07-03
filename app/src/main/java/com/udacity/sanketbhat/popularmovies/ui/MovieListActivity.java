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

package com.udacity.sanketbhat.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.sanketbhat.popularmovies.R;
import com.udacity.sanketbhat.popularmovies.adapter.MovieClickListener;
import com.udacity.sanketbhat.popularmovies.model.Movie;
import com.udacity.sanketbhat.popularmovies.model.SortOrder;
import com.udacity.sanketbhat.popularmovies.util.PreferenceUtils;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity implements MovieClickListener, FragmentManager.OnBackStackChangedListener {

    public boolean mTwoPane;
    private Menu mOptionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        //Prepare toolbar and set title
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.movie_detail_container) != null) {
            //the detail fragment shown only in the larger displays.
            mTwoPane = true;
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (savedInstanceState == null) {
            MovieListFragment fragment = new MovieListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_list_container, fragment)
                    .commit();
        }

        onBackStackChanged();
    }


    @Override
    protected void onStop() {
        //Cancel all requests created within adapter.
        Picasso.with(this)
                .cancelTag(MovieListActivity.class.getSimpleName());
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mOptionMenu = menu;
        setupOptionMenu();
        return true;
    }

    private void setupOptionMenu() {
        int sortOrder = PreferenceUtils.getPreferredSortOrder(this);
        switch (sortOrder) {
            case SortOrder.SORT_ORDER_POPULAR:
                mOptionMenu.findItem(R.id.menu_sort_popularity).setChecked(true);
                break;

            case SortOrder.SORT_ORDER_TOP_RATED:
                mOptionMenu.findItem(R.id.menu_sort_rating).setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_popularity:
                item.setChecked(true);
                return changeSortOrderTo(SortOrder.SORT_ORDER_POPULAR);

            case R.id.menu_sort_rating:
                item.setChecked(true);
                return changeSortOrderTo(SortOrder.SORT_ORDER_TOP_RATED);

            case R.id.menu_action_favorites:
                MovieListFavoritesFragment movieListFavoritesFragment = new MovieListFavoritesFragment();
                getSupportFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.movie_list_container, movieListFavoritesFragment)
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.menu_main_credits:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.menu_main_credits)
                        .setMessage(R.string.credits_string)
                        .setPositiveButton(R.string.credit_dialog_button, (dialog1, which) -> dialog1.dismiss()).create();
                dialog.show();
                break;

            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    getSupportFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean changeSortOrderTo(int sortOrder) {
        if (PreferenceUtils.getPreferredSortOrder(this) != sortOrder) {
            PreferenceUtils.setPreferredSortOrder(this, sortOrder);
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onClickItem(View v, Movie movie) {
        //Pass the clicked movie item.
        //It will be removed in stage 2.
        Bundle arguments = new Bundle();
        arguments.putParcelable(MovieDetailFragment.ARG_ITEM, movie);

        //If twoPane layout just inflate/ replace the fragment at the right side.
        if (mTwoPane) {
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            //Prepare items for shared element transactions
            ImageView imageView = v.findViewById(R.id.movie_grid_item_image);
            Bundle transition = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this, imageView, getString(R.string.shared_element_transition_name))
                    .toBundle();

            //If not twoPane layout start the MovieDetailActivity
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtras(arguments);
            startActivity(intent, transition);
        }
    }

    @Override
    public void onBackStackChanged() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (backStackEntryCount <= 0) {
                actionBar.setDisplayHomeAsUpEnabled(false);
            } else {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }
}
