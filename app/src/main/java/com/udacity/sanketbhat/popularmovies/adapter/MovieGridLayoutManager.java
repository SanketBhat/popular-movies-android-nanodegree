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
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;

/**
 * The grid layout manager that dynamically set the span size of the items
 * In the two pane layout, the items per a row is limit to 2. and in single
 * pane layout it is dynamically calculated using the screen width and item width
 */
public class MovieGridLayoutManager extends GridLayoutManager {
    private static final int SINGLE_GRID_ITEM_WIDTH = 160;
    private int movieItemSpanCount;
    private static final int TWO_PANE_SPAN_SIZE = 2;
    private static final int TWO_PANE_MINIMUM_SIZE = 720;

    public MovieGridLayoutManager(Context context, MovieListAdapter adapter) {
        super(context, 1, GridLayoutManager.VERTICAL, false);
        movieItemSpanCount = getCustomSpanCount(context);
        setSpanCount(movieItemSpanCount);
        setSpanSizeLookup(new MovieSpanSizeLookup(adapter));
    }

    private int getCustomSpanCount(Context mContext) {
        //Calculate device width dynamically
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        float width = metrics.widthPixels / metrics.density;

        //If width is more than 720dp, its twoPane layout
        //And it can have at most 2 item per row
        if (width >= TWO_PANE_MINIMUM_SIZE) return TWO_PANE_SPAN_SIZE;

        //If not twoPane layout, calculate calculate span size
        //160 is the fixed width for a single movie item.
        return (int) width / SINGLE_GRID_ITEM_WIDTH;
    }

    class MovieSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

        private MovieListAdapter mAdapter;

        MovieSpanSizeLookup(MovieListAdapter adapter) {
            this.mAdapter = adapter;
        }

        @Override
        public int getSpanSize(int position) {
            switch (mAdapter.getItemViewType(position)) {
                //If list is showing the progress, then it should occupy the complete device width
                case MovieListAdapter.VIEW_TYPE_PROGRESS:
                    return movieItemSpanCount;

                case MovieListAdapter.VIEW_TYPE_MOVIE:
                    return 1;

                default:
                    return 1;
            }
        }
    }
}
