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

import android.animation.LayoutTransition;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.sanketbhat.popularmovies.R;
import com.udacity.sanketbhat.popularmovies.adapter.ReviewListAdapter;
import com.udacity.sanketbhat.popularmovies.adapter.VideoClickListener;
import com.udacity.sanketbhat.popularmovies.adapter.VideoListAdapter;
import com.udacity.sanketbhat.popularmovies.databinding.ActivityMovieDetailContentBinding;
import com.udacity.sanketbhat.popularmovies.model.Movie;
import com.udacity.sanketbhat.popularmovies.model.Video;
import com.udacity.sanketbhat.popularmovies.util.ImageUrlBuilder;

import java.util.List;
import java.util.Locale;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment implements VideoClickListener {

    public static final String ARG_ITEM = "movieItem";
    private static final String YOUTUBE_LINK_TEMPLATE = "https://www.youtube.com/watch?v=";
    private Movie movie;
    private MovieDetailViewModel viewModel;
    private boolean isFavorite;
    private Menu optionMenu;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

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
        rootView.getViewTreeObserver().addOnPreDrawListener(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                    getActivity() != null &&
                    getActivity().getClass() == MovieDetailActivity.class) {
                //Start shared transition on Lollipop and higher devices.
                getActivity().startPostponedEnterTransition();
            }
            return true;
        });

        ((ConstraintLayout) rootView).getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        if (movie != null && mBinding != null && getContext() != null) {
            //Loading views with data
            Picasso.with(getContext())
                    .load(ImageUrlBuilder.getPosterUrlString(movie.getPosterPath()))
                    .error(R.drawable.ic_movie_grid_item_image_error)
                    .into(mBinding.movieDetailPoster);
            mBinding.setMovie(movie);

            mBinding.buttonFavorites.setOnClickListener((v) -> {
                if (isFavorite) viewModel.removeFromFavorites(movie);
                else viewModel.insertIntoFavorites(movie);
            });

            final VideoListAdapter videoListAdapter = new VideoListAdapter(getContext(), this);
            videoListAdapter.setShowLoading(true);
            mBinding.videosList.setAdapter(videoListAdapter);
            mBinding.videosList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

            final ReviewListAdapter reviewListAdapter = new ReviewListAdapter(getContext());
            reviewListAdapter.setShowLoading(true);
            mBinding.reviewsList.setAdapter(reviewListAdapter);
            mBinding.reviewsList.setNestedScrollingEnabled(false);
            mBinding.reviewsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

            viewModel.isFavorite(movie.getId()).observe(this, movie1 -> {
                isFavorite = movie1 != null;
                if (isFavorite)
                    mBinding.buttonFavorites.setImageResource(R.drawable.ic_favorite_white);
                else mBinding.buttonFavorites.setImageResource(R.drawable.ic_favorite_border_white);
            });

            viewModel.getMovie(movie.getId()).observe(this, movie -> {
                videoListAdapter.setContentList(movie == null ? null : movie.getVideos());
                reviewListAdapter.setContentList(movie == null ? null : movie.getReviews());
                if (movie != null && movie.getReviewResponse() != null && movie.getVideoResponse() != null) {
                    this.movie.setVideoResponse(movie.getVideoResponse());
                    this.movie.setReviewResponse(movie.getReviewResponse());
                    setupShareButton();
                }
            });
        } else Log.i(this.getClass().getSimpleName(), "Can't bind data. movie/context is null!");

        return rootView;
    }

    private void setupShareButton() {
        List<Video> videos = movie.getVideos();
        if (videos != null && videos.size() > 0 && videos.get(0).getSite().equalsIgnoreCase("youtube")) {
            //Enable sharing for first trailer
            if (optionMenu != null && optionMenu.findItem(R.id.action_share) != null)
                optionMenu.findItem(R.id.action_share).setVisible(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        optionMenu = menu;
        setupShareButton();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            if (getActivity() != null
                    && movie.getVideos() != null
                    && movie.getVideos().size() > 0
                    && movie.getVideos().get(0).getSite().equalsIgnoreCase("youtube")) {
                String url = YOUTUBE_LINK_TEMPLATE + movie.getVideos().get(0).getKey();
                String textToShare = String.format(Locale.getDefault(), getString(R.string.share_text_template), url);
                ShareCompat.IntentBuilder.from(getActivity())
                        .setText(textToShare)
                        .setChooserTitle(R.string.intent_chooser_title)
                        .setType("text/plain")
                        .startChooser();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onVideoClicked(Video video) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_LINK_TEMPLATE + video.getKey()));
        if (getContext() != null && getContext().getPackageManager().resolveActivity(intent, 0) != null) {
            startActivity(intent);
            return;
        }
        Toast.makeText(getContext(), R.string.video_clicked_no_app_error, Toast.LENGTH_SHORT).show();
    }
}
