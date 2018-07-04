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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.sanketbhat.popularmovies.R;
import com.udacity.sanketbhat.popularmovies.model.Review;

public class ReviewListAdapter extends RecyclerViewAdapterTemplate<ReviewListViewHolder, Review> {

    public ReviewListAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    ReviewListViewHolder getNormalViewHolder(ViewGroup parent) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_review_item, parent, false);
        return new ReviewListViewHolder(rootView);
    }

    @Override
    void normalBinding(ReviewListViewHolder holder, int position) {
        Review review = getContents().get(position);
        holder.review.setText(review.getContent());
        holder.author.setText(review.getAuthor());
    }

    @Override
    String getEmptyLayoutMessage() {
        return getContext().getString(R.string.empty_list_message);
    }
}
