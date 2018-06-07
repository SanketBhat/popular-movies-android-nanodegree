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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

    MovieListViewModel model;
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
    GridLayoutManager gridLayoutManager;

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

        model = ViewModelProviders.of(this).get(MovieListViewModel.class);
        spanCount = getSpanCount();

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

    private void observeViewModel() {
        model.getLoadingIndicator().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean loading) {
                if (loading != null) adapter.setLoading(loading);
            }
        });

        model.getFailureIndicator().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                if (adapter.isLoading()) {
                    //Error when loading nextPage
                    //TODO: Handle this case
                } else if (adapter.getItemCount() == 0) {
                    showErrorLayout();
                }
            }
        });

        model.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                showMovieList();
                adapter.swapMovies(movies);
            }
        });
    }

    private void loadFirstPage(boolean retrying) {
        showMovieLoading();
        model.getFirstPage(PreferenceUtils.getPreferredSortOrder(this), retrying);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //Initialize adapter with null movie items. Because it will be set once loader executed
        adapter = new RecyclerViewAdapter(this, null, this);
        recyclerView.setAdapter(adapter);

        //Set the gridLayoutManager with span count calculated dynamically

        gridLayoutManager = new GridLayoutManager(this,
                spanCount,
                GridLayoutManager.VERTICAL,
                false);
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

    private int getSpanCount() {
        //If twoPane can have at most 2 items per row.
        if (mTwoPane) {
            return 2;
        }

        //If not twoPane calculate the span dynamically.
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float width = metrics.widthPixels / metrics.density;

        //160 is the fixed width for a single movie item.
        return (int) width / 160;
    }

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
        Picasso.with(this)
                .cancelTag(MovieListActivity.class);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeSortOrderTo(int sortOrder) {
        if (PreferenceUtils.getPreferredSortOrder(this) != sortOrder) {
            PreferenceUtils.setPreferredSortOrder(this, sortOrder);
            adapter.swapMovies(null);
            loadFirstPage(false);
        }
    }

    @Override
    public void onClickItem(Movie movie) {

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

            //If not twoPane layout start the MovieDetailActivity
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtras(arguments);
            startActivity(intent);
        }
    }

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
