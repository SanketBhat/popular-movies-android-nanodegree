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

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.sanketbhat.popularmovies.R;
import com.udacity.sanketbhat.popularmovies.adapter.MovieClickListener;
import com.udacity.sanketbhat.popularmovies.adapter.MovieGridLayoutManager;
import com.udacity.sanketbhat.popularmovies.adapter.MovieListAdapter;
import com.udacity.sanketbhat.popularmovies.databinding.ActivityMovieListFragmentBinding;
import com.udacity.sanketbhat.popularmovies.model.SortOrder;
import com.udacity.sanketbhat.popularmovies.util.PreferenceUtils;

public class MovieListFragment extends Fragment {
    private MovieListViewModel viewModel;
    private ActivityMovieListFragmentBinding mBinding;
    private MovieGridLayoutManager gridLayoutManager;
    private MovieListAdapter adapter;

    public MovieListFragment() {
        //Required public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setActivityTitle();

        View rootView = inflater.inflate(R.layout.activity_movie_list_fragment, container, false);
        mBinding = DataBindingUtil.bind(rootView);
        if (mBinding != null) {
            setupRecyclerView(mBinding.movieList);
            mBinding.movieListErrorRetryButton.setOnClickListener(v -> loadFirstPage(true));
            loadFirstPage(false);
            observeViewModel();
        }

        return rootView;
    }

    private void setActivityTitle() {
        if (getActivity() != null) {
            int sortOrder = PreferenceUtils.getPreferredSortOrder(getActivity().getApplicationContext());
            switch (sortOrder) {
                case SortOrder.SORT_ORDER_POPULAR:
                    getActivity().setTitle(getString(R.string.title_popular_movie));
                    break;

                case SortOrder.SORT_ORDER_TOP_RATED:
                    getActivity().setTitle(getString(R.string.title_top_rated_movie));
                    break;

                default:
                    getActivity().setTitle(R.string.app_name);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_popularity:
            case R.id.menu_sort_rating:
                gridLayoutManager.scrollToPosition(0);
                adapter.swapMovies(null);
                loadFirstPage(false);
                setActivityTitle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //Initialize adapter with null movie items. Because it will be set once it is available
        adapter = new MovieListAdapter(getContext(), null, (MovieClickListener) getActivity());
        recyclerView.setAdapter(adapter);

        gridLayoutManager = new MovieGridLayoutManager(getContext(), adapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        //Scroll listener for loading next page once user reach end of the page.
        recyclerView.addOnScrollListener(new MovieListScrollListener());
    }

    private void loadFirstPage(boolean retrying) {
        //Loading first page with the loading layout
        showMovieLoading();
        viewModel.getFirstPage(PreferenceUtils.getPreferredSortOrder(getContext()), retrying);
    }

    private void observeViewModel() {
        //Observe loading indicator so that adapter can display loading for next page.
        viewModel.getLoadingIndicator().observe(this, loading -> {
            if (loading != null) adapter.setLoading(loading);
        });

        //Failure indicator for showing error message that user can understand
        viewModel.getFailureIndicator().observe(this, aVoid -> {
            if (adapter.isLoading()) {
                //Error when loading nextPage
                Log.i("MovieListActivity", "Error when loading next page");
            } else if (adapter.getItemCount() == 0) {
                showErrorLayout();
            }
        });

        //Whenever the movie list changes it notifies the adapter
        viewModel.getMovies().observe(this, movies -> {
            showMovieList();
            adapter.swapMovies(movies);
        });
    }

    //Helper methods for showing three different layouts list, loading, error.
    private void showMovieList() {
        mBinding.movieList.setVisibility(View.VISIBLE);
        mBinding.movieLoadProgress.setVisibility(View.GONE);
        mBinding.movieListErrorLayout.setVisibility(View.GONE);
    }

    private void showMovieLoading() {
        mBinding.movieList.setVisibility(View.GONE);
        mBinding.movieLoadProgress.setVisibility(View.VISIBLE);
        mBinding.movieListErrorLayout.setVisibility(View.GONE);
    }

    private void showErrorLayout() {
        mBinding.movieList.setVisibility(View.GONE);
        mBinding.movieLoadProgress.setVisibility(View.GONE);
        mBinding.movieListErrorLayout.setVisibility(View.VISIBLE);
    }

    //Custom scroll listener for endless list implementation
    class MovieListScrollListener extends RecyclerView.OnScrollListener {
        private final Handler handler = new Handler();

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            final int itemCount = gridLayoutManager.getItemCount();
            final int visibleItemCount = gridLayoutManager.getChildCount();
            final int lastVisiblePosition = gridLayoutManager.findLastVisibleItemPosition();

            if ((visibleItemCount + lastVisiblePosition + 4) >= itemCount && !viewModel.isLastPage()) {
                handler.post(() -> viewModel.getNextPage());
            }
        }
    }
}
