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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.sanketbhat.popularmovies.R;
import com.udacity.sanketbhat.popularmovies.model.Movie;
import com.udacity.sanketbhat.popularmovies.util.MovieUrlBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM = "movieItem";
    private Movie movie;

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
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_movie_detail_content, container, false);

        ImageView posterImage = rootView.findViewById(R.id.movieDetailPoster);
        TextView title = rootView.findViewById(R.id.movieDetailName);
        TextView genre = rootView.findViewById(R.id.movieDetailGenre);
        TextView plot = rootView.findViewById(R.id.movieDetailPlotSynopsis);
        TextView releaseDate = rootView.findViewById(R.id.movieDetailReleaseDate);
        TextView ratingText = rootView.findViewById(R.id.movieDetailRatingText);
        if (movie != null) {
            Picasso.with(getContext())
                    .load(MovieUrlBuilder.getPosterUrlString(movie.getPosterPath()))
                    .into(posterImage);
            title.setText(movie.getTitle());
            genre.setText(movie.getGenres());
            plot.setText(movie.getOverview());

            try {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = simpleDateFormat.parse(movie.getReleaseDate());
                String dateText = SimpleDateFormat.getDateInstance().format(date);
                releaseDate.setText(dateText);
            } catch (ParseException e) {
                releaseDate.setText(R.string.movie_detail_date_error);
                e.printStackTrace();
            }

            float rating = (float) movie.getVoteAverage() / 2.0f;
            String ratingString = "(" + String.format(Locale.getDefault(), "%.1f", rating) + "/5)";
            ratingText.setText(ratingString);
        }

        return rootView;
    }
}
