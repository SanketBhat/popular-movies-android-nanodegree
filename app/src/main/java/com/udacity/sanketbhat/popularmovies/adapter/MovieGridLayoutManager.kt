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
import androidx.recyclerview.widget.GridLayoutManager

/**
 * The grid layout manager that dynamically set the span size of the items
 * In the two pane layout, the items per a row is limit to 2. and in single
 * pane layout it is dynamically calculated using the screen width and item width
 */
class MovieGridLayoutManager(context: Context, adapter: MovieListAdapter) : GridLayoutManager(context, 1, VERTICAL, false) {
    private val movieItemSpanCount: Int
    private fun getCustomSpanCount(mContext: Context): Int {
        //Calculate device width dynamically
        val metrics = mContext.resources.displayMetrics
        val width = metrics.widthPixels / metrics.density

        //If width is more than 720dp, its twoPane layout
        //And it can have at most 2 item per row
        return if (width >= TWO_PANE_MINIMUM_SIZE) TWO_PANE_SPAN_SIZE else width.toInt() / SINGLE_GRID_ITEM_WIDTH

        //If not twoPane layout, calculate calculate span size
        //160 is the fixed width for a single movie item.
    }

    internal inner class MovieSpanSizeLookup(private val mAdapter: MovieListAdapter) : SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (mAdapter.getItemViewType(position)) {
                MovieListAdapter.Companion.VIEW_TYPE_PROGRESS -> movieItemSpanCount
                MovieListAdapter.Companion.VIEW_TYPE_MOVIE -> 1
                else -> 1
            }
        }
    }

    companion object {
        private const val SINGLE_GRID_ITEM_WIDTH = 160
        private const val TWO_PANE_SPAN_SIZE = 2
        private const val TWO_PANE_MINIMUM_SIZE = 720
    }

    init {
        movieItemSpanCount = getCustomSpanCount(context)
        spanCount = movieItemSpanCount
        spanSizeLookup = MovieSpanSizeLookup(adapter)
    }
}