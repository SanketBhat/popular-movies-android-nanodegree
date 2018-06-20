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
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.sanketbhat.popularmovies.R;
import com.udacity.sanketbhat.popularmovies.adapter.ItemClickListener;
import com.udacity.sanketbhat.popularmovies.adapter.MovieGridLayoutManager;
import com.udacity.sanketbhat.popularmovies.adapter.RecyclerViewAdapter;
import com.udacity.sanketbhat.popularmovies.databinding.ActivityMovieListFavoritesBinding;
import com.udacity.sanketbhat.popularmovies.model.Movie;

import java.util.List;

public class MovieListFavoritesFragment extends Fragment {

    private ActivityMovieListFavoritesBinding mBinding;
    private MovieListViewModel viewModel;

    public MovieListFavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null)
            viewModel = ViewModelProviders.of(getActivity()).get(MovieListViewModel.class);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() != null) getActivity().setTitle("Favorites");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_movie_list_favorites, container, false);
        mBinding = DataBindingUtil.bind(rootView);
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), null, (ItemClickListener) getActivity());

        MovieGridLayoutManager gridLayoutManager = new MovieGridLayoutManager(getContext(), adapter);
        mBinding.favoritesMovieList.setLayoutManager(gridLayoutManager);
        mBinding.favoritesMovieList.setAdapter(adapter);
        viewModel.getFavorites().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                adapter.swapMovies(movies);
            }
        });
        return rootView;

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.menu_action_favorites);
        if (item != null) item.setVisible(false);
        item = menu.findItem(R.id.menu_action_sort);
        if (item != null) item.setVisible(false);
    }
}
