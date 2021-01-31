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
package com.udacity.sanketbhat.popularmovies.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import com.udacity.sanketbhat.popularmovies.R
import com.udacity.sanketbhat.popularmovies.model.Movie
import com.udacity.sanketbhat.popularmovies.ui.MovieListActivity
import com.udacity.sanketbhat.popularmovies.util.ImageUrlBuilder

class MovieListAdapter(private val mContext: Context, private var movies: List<Movie>?, private val clickListener: MovieClickListener?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //Boolean flag for showing or not showing the loading indicator at the end of the grid.
    private var loading = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MOVIE -> {
                val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.movie_grid_item, parent, false)
                ViewHolder(inflatedView)
            }
            VIEW_TYPE_PROGRESS -> {
                val progressView = LayoutInflater.from(parent.context).inflate(R.layout.grid_loading_indicator, parent, false)
                ProgressViewHolder(progressView)
            }
            else -> throw IllegalArgumentException("Unsupported View type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_MOVIE -> if (holder is ViewHolder) {
                //Bind view with data
                val viewHolder = holder
                val movie = movies!![position]
                viewHolder.movieGenre.text = movie.genresString
                viewHolder.movieName.text = movie.title
                Picasso.with(mContext)
                        .load(ImageUrlBuilder.getPosterUrlString(movie.posterPath))
                        .tag(MovieListActivity::class.java.simpleName)
                        .error(R.drawable.ic_movie_grid_item_image_error)
                        .placeholder(R.drawable.ic_loading_indicator)
                        .into(viewHolder.target)
            }
            VIEW_TYPE_PROGRESS -> {
            }
            else -> throw IllegalArgumentException("Unsupported View type")
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        //Cleanup resources after viewHolder recycled
        if (holder is ViewHolder) holder.cleanUp()
    }

    override fun getItemCount(): Int {
        //If movies = null return 0, or if next page is loading return size of movies + 1
        //Else its normal, return movies.size()
        return if (movies == null) 0 else if (loading) movies!!.size + 1 else movies!!.size
    }

    override fun getItemViewType(position: Int): Int {
        //If loading new page, return last element type as loading indicator
        return if (position == movies!!.size && isLoading()) VIEW_TYPE_PROGRESS else VIEW_TYPE_MOVIE
    }

    fun isLoading(): Boolean {
        return loading
    }

    fun setLoading(loading: Boolean) {
        if (this.loading != loading) {
            //If loading is finished due to error or successful response-
            //notify the list to hide the loading indicator
            this.loading = loading
            notifyDataSetChanged()
        }
    }

    //Swap the new available list with the current one
    fun swapMovies(movies: List<Movie>?) {
        this.movies = movies
        notifyDataSetChanged()
    }

    //ViewHolder for normal movie item.
    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val rootView: CardView = itemView as CardView
        val moviePoster: ImageView = itemView.findViewById(R.id.movie_grid_item_image)
        val movieName: TextView = itemView.findViewById(R.id.movie_grid_item_title)
        val movieGenre: TextView = itemView.findViewById(R.id.movie_grid_item_genre)

        init {
            itemView.setOnClickListener(this)
        }

        private var cardBackground = 0
        val target: Target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                try {
                    if (cardBackground == 0) {
                        Palette.from(bitmap).generate { palette: Palette? ->
                            val swatch = palette!!.vibrantSwatch
                            if (swatch != null) {
                                rootView.setCardBackgroundColor(swatch.rgb.also { cardBackground = it })
                                movieName.setTextColor(swatch.bodyTextColor)
                                movieGenre.setTextColor(swatch.titleTextColor)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(mContext, "Error loading cardView background color", Toast.LENGTH_SHORT).show()
                }
                moviePoster.setImageBitmap(bitmap)
            }

            override fun onBitmapFailed(errorDrawable: Drawable) {
                moviePoster.setImageDrawable(errorDrawable)
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
        }

        override fun onClick(v: View) {
            clickListener?.onClickItem(v, movies!![adapterPosition])
        }

        fun cleanUp() {
            //Cancel request once viewHolder recycled.
            Picasso.with(moviePoster.context)
                    .cancelRequest(moviePoster)
            moviePoster.setImageDrawable(null)
            cardBackground = 0
        }
    }

    //View Holder for progress indicator
    internal inner class ProgressViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)
    companion object {
        //Two view types
        // -> 1. For normal movie grid item.
        // -> 2. For the loading indicator at the end.
        const val VIEW_TYPE_MOVIE = 1
        const val VIEW_TYPE_PROGRESS = 2
    }
}