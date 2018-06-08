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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;
import com.udacity.sanketbhat.popularmovies.R;
import com.udacity.sanketbhat.popularmovies.adapter.RecyclerViewAdapter;
import com.udacity.sanketbhat.popularmovies.model.Movie;
import com.udacity.sanketbhat.popularmovies.model.SortOrder;
import com.udacity.sanketbhat.popularmovies.util.PreferenceUtils;

import java.util.List;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity implements
        RecyclerViewAdapter.ItemClickListener {

    private static final int SINGLE_GRID_ITEM_WIDTH = 160;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private int spanCount;
    private View errorLayout;
    private GridLayoutManager gridLayoutManager;
    private MovieListViewModel model;
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

        //Get the viewModel
        model = ViewModelProviders.of(this).get(MovieListViewModel.class);

        //Calculate spanCount for gridLayoutManager
        spanCount = getSpanCount();

        //Initialize view objects
        progressBar = findViewById(R.id.movie_load_progress);
        errorLayout = findViewById(R.id.movie_list_error_layout);
        Button retryButton = findViewById(R.id.movie_list_error_retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFirstPage(true);
            }
        });

        //Initialize the recyclerview view object
        recyclerView = findViewById(R.id.movie_list);
        setupRecyclerView(recyclerView);

        //Start the loader for the fresh data.
        loadFirstPage(false);
        observeViewModel();
    }

    private int getSpanCount() {
        //If twoPane can have at most 2 items per row.
        //Because for twoPane layout the list width is fixed (320dp)
        if (mTwoPane) {
            return 2;
        }
        //If not twoPane calculate the span dynamically.
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float width = metrics.widthPixels / metrics.density;

        //160 is the fixed width for a single movie item.
        return (int) width / SINGLE_GRID_ITEM_WIDTH;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //Initialize adapter with null movie items. Because it will be set once it is available
        adapter = new RecyclerViewAdapter(this, null, this);
        recyclerView.setAdapter(adapter);

        //Set the gridLayoutManager with span count calculated dynamically
        gridLayoutManager = new GridLayoutManager(this,
                spanCount,
                GridLayoutManager.VERTICAL,
                false);

        //Span size look up for giving full width for loading indicator.
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case RecyclerViewAdapter.VIEW_TYPE_PROGRESS:
                        return spanCount;

                    case RecyclerViewAdapter.VIEW_TYPE_MOVIE:
                        return 1;

                    default:
                        return 1;
                }
            }
        });

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        //Scroll listener for loading next page once user reach end of the page.
        recyclerView.addOnScrollListener(new MovieListScrollListener());
    }

    private void loadFirstPage(boolean retrying) {
        //Loading first page with the loading layout
        showMovieLoading();
        model.getFirstPage(PreferenceUtils.getPreferredSortOrder(this), retrying);
    }

    private void observeViewModel() {
        //Observe loading indicator so that adapter can display loading for next page.
        model.getLoadingIndicator().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean loading) {
                if (loading != null) adapter.setLoading(loading);
            }
        });

        //Failure indicator for showing error message that user can understand
        model.getFailureIndicator().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                if (adapter.isLoading()) {
                    //Error when loading nextPage
                    Log.i("MovieListActivity", "Error when loading next page");
                } else if (adapter.getItemCount() == 0) {
                    showErrorLayout();
                }
            }
        });

        //Whenever the movie list changes it notifies the adapter
        model.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                showMovieList();
                adapter.swapMovies(movies);
            }
        });
    }

    //Helper methods for showing three different layouts list, loading, error.
    private void showMovieList() {
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);
    }

    private void showMovieLoading() {
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);
    }

    private void showErrorLayout() {
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        //Cancel all requests created within adapter.
        Picasso.with(this)
                .cancelTag(MovieListActivity.class);
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
                mOptionMenu.findItem(R.id.menu_sort_popularity).setEnabled(false);
                mOptionMenu.findItem(R.id.menu_sort_rating).setEnabled(true);
                break;

            case SortOrder.SORT_ORDER_TOP_RATED:
                mOptionMenu.findItem(R.id.menu_sort_popularity).setEnabled(true);
                mOptionMenu.findItem(R.id.menu_sort_rating).setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_popularity:
                changeSortOrderTo(SortOrder.SORT_ORDER_POPULAR);
                return true;

            case R.id.menu_sort_rating:
                changeSortOrderTo(SortOrder.SORT_ORDER_TOP_RATED);
                return true;

            case R.id.menu_main_credits:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.menu_main_credits)
                        .setMessage(R.string.credits_string)
                        .setPositiveButton(R.string.credit_dialog_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeSortOrderTo(int sortOrder) {
        if (PreferenceUtils.getPreferredSortOrder(this) != sortOrder) {
            PreferenceUtils.setPreferredSortOrder(this, sortOrder);
            setupOptionMenu();
            adapter.swapMovies(null);
            loadFirstPage(false);
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

    //Custom scroll listener for endless list implementation
    class MovieListScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int itemCount = gridLayoutManager.getItemCount();
            int visibleItemCount = gridLayoutManager.getChildCount();
            int lastVisiblePosition = gridLayoutManager.findLastVisibleItemPosition();

            model.movieListScrolled(visibleItemCount, lastVisiblePosition, itemCount);
        }
    }
}
