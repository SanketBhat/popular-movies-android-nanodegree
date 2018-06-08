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

package com.udacity.sanketbhat.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.sanketbhat.popularmovies.R;
import com.udacity.sanketbhat.popularmovies.model.Movie;
import com.udacity.sanketbhat.popularmovies.ui.MovieListActivity;
import com.udacity.sanketbhat.popularmovies.util.ImageUrlBuilder;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter {

    //Two view types
    // -> 1. For normal movie grid item.
    // -> 2. For the loading indicator at the end.
    public static final int VIEW_TYPE_MOVIE = 1;
    public static final int VIEW_TYPE_PROGRESS = 2;

    private final ItemClickListener clickListener;
    private final Context mContext;
    private List<Movie> movies;

    //Boolean flag for showing or not showing the loading indicator at the end of the grid.
    private boolean loading = false;

    public RecyclerViewAdapter(Context context, List<Movie> movies, ItemClickListener clickListener) {
        this.movies = movies;
        this.clickListener = clickListener;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_MOVIE:
                View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_grid_item, parent, false);
                return new ViewHolder(inflatedView);

            case VIEW_TYPE_PROGRESS:
                View progressView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_loading_indicator, parent, false);
                return new ProgressViewHolder(progressView);

            default:
                throw new IllegalArgumentException("Unsupported View type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_MOVIE:
                if (holder instanceof ViewHolder) {
                    //Bind view with data
                    final ViewHolder viewHolder = (ViewHolder) holder;
                    viewHolder.movieGenre.setText(movies.get(position).getGenres());
                    viewHolder.movieName.setText(movies.get(position).getTitle());
                    Picasso.with(mContext)
                            .load(ImageUrlBuilder.getPosterUrlString(movies.get(position).getPosterPath()))
                            .tag(MovieListActivity.class)
                            .error(R.drawable.ic_movie_grid_item_image_error)
                            .placeholder(R.drawable.ic_loading_indicator)
                            .into(viewHolder.moviePoster);
                }
                break;

            case VIEW_TYPE_PROGRESS:
                //progress is showing
                break;

            default:
                throw new IllegalArgumentException("Unsupported View type");
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        //Cleanup resources after viewHolder recycled
        if (holder instanceof ViewHolder) ((ViewHolder) holder).cleanUp();
    }

    @Override
    public int getItemCount() {
        //If movies = null return 0, or if next page is loading return size of movies + 1
        //Else its normal to return movies.size()
        if (movies == null) return 0;
        else if (loading) return movies.size() + 1;
        else return movies.size();
    }

    @Override
    public int getItemViewType(int position) {
        //If loading new page, return last element type as loading indicator
        if ((position == movies.size()) && isLoading()) return VIEW_TYPE_PROGRESS;
        else return VIEW_TYPE_MOVIE;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        if (this.loading != loading) {
            this.loading = loading;
            notifyDataSetChanged();
        }
    }

    public void swapMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onClickItem(View v, Movie movie);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView moviePoster;
        final TextView movieName;
        final TextView movieGenre;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            moviePoster = itemView.findViewById(R.id.movie_grid_item_image);
            movieName = itemView.findViewById(R.id.movie_grid_item_title);
            movieGenre = itemView.findViewById(R.id.movie_grid_item_genre);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClickItem(v, movies.get(getAdapterPosition()));
            }
        }

        private void cleanUp() {
            //Cancel request once viewHolder recycled.
            Picasso.with(moviePoster.getContext())
                    .cancelRequest(moviePoster);
            moviePoster.setImageDrawable(null);
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }
}
