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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.squareup.picasso.Picasso;
import com.udacity.sanketbhat.popularmovies.R;
import com.udacity.sanketbhat.popularmovies.databinding.ActivityMovieDetailContentBinding;
import com.udacity.sanketbhat.popularmovies.model.Movie;
import com.udacity.sanketbhat.popularmovies.util.ImageUrlBuilder;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    public static final String ARG_ITEM = "movieItem";
    private Movie movie;
    private MovieDetailViewModel viewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_ITEM)) {
            movie = getArguments().getParcelable(ARG_ITEM);
        }

        if (getContext() != null)
            viewModel = ViewModelProviders.of(this).get(MovieDetailViewModel.class);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_movie_detail_content, container, false);
        ActivityMovieDetailContentBinding mBinding = DataBindingUtil.bind(rootView);
        rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                        getActivity() != null &&
                        getActivity().getClass() == MovieDetailActivity.class) {
                    //Start shared transition on Lollipop and higher devices.
                    getActivity().startPostponedEnterTransition();
                }
                return true;
            }
        });

        if (movie != null && mBinding != null) {
            //Loading views with data
            Picasso.with(getContext())
                    .load(ImageUrlBuilder.getPosterUrlString(movie.getPosterPath()))
                    .into(mBinding.movieDetailPoster);
            mBinding.setMovie(movie);

            if (movie.getVideos() == null || movie.getReviews() == null) {
                viewModel.getMovie(movie.getId()).observe(this, new Observer<Movie>() {
                    @Override
                    public void onChanged(@Nullable Movie movie) {
                        Log.e("loaded", "do some stuff");
                    }
                });
            }
        }

        return rootView;
    }
}
