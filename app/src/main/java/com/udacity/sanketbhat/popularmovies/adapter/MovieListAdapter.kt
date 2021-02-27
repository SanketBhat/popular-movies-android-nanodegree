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
import androidx.paging.PagingDataAdapter
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import com.udacity.sanketbhat.popularmovies.R
import com.udacity.sanketbhat.popularmovies.model.Movie
import com.udacity.sanketbhat.popularmovies.ui.MovieListActivity
import com.udacity.sanketbhat.popularmovies.util.ImageUrlBuilder

class MovieListAdapter(
        private val mContext: Context,
        private val clickListener: MovieClickListener?
) : PagingDataAdapter<Movie, RecyclerView.ViewHolder>(MOVIE_COMPARATOR) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.movie_grid_item, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            //Bind view with data
            val movie = getItem(position)
            holder.bind(movie)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_MOVIE
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        //Cleanup resources after viewHolder recycled
        if (holder is ViewHolder) holder.cleanUp()
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

        fun bind(movie: Movie?) {
            movie?.let {
                movieGenre.text = movie.genresString
                movieName.text = movie.title
                Picasso.with(mContext)
                        .load(ImageUrlBuilder.getPosterUrlString(movie.posterPath))
                        .tag(MovieListActivity::class.java.simpleName)
                        .error(R.drawable.ic_movie_grid_item_image_error)
                        .placeholder(R.drawable.ic_loading_indicator)
                        .into(target)
            }
        }

        private var cardBackground = 0
        private val target: Target = object : Target {
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
            getItem(bindingAdapterPosition)?.let {
                clickListener?.onClickItem(v, it)
            }

        }

        fun cleanUp() {
            //Cancel request once viewHolder recycled.
            Picasso.with(moviePoster.context)
                    .cancelRequest(moviePoster)
            moviePoster.setImageDrawable(null)
            cardBackground = 0
        }
    }

    companion object {
        //Two view types
        // -> 1. For normal movie grid item.
        // -> 2. For the loading indicator at the end.
        const val VIEW_TYPE_MOVIE = 1
        const val VIEW_TYPE_PROGRESS = 2
        private val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                //Comparing only visible items
                return (oldItem.id == newItem.id && oldItem.genresString == newItem.genresString && oldItem.posterPath == newItem.posterPath)
            }
        }
    }
}