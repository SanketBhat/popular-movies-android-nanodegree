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
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.udacity.sanketbhat.popularmovies.R;
import com.udacity.sanketbhat.popularmovies.model.Video;

import java.util.Locale;

public class VideoListAdapter extends RecyclerViewAdapterTemplate<VideoListViewHolder, Video> {

    private static final String YOUTUBE_THUMBNAIL_TEMPLATE = "http://img.youtube.com/vi/%s/mqdefault.jpg";
    private static final String YOUTUBE_VIDEO_SOURCE = "youtube";
    private final VideoClickListener clickListener;
    private final Context mContext;

    public VideoListAdapter(@NonNull Context context, @NonNull VideoClickListener clickListener) {
        super(context);
        mContext = context;
        this.clickListener = clickListener;
    }

    @Override
    VideoListViewHolder getNormalViewHolder(ViewGroup parent) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_video_item, parent, false);
        return new VideoListViewHolder(rootView);
    }

    @Override
    void normalBinding(VideoListViewHolder holder, int position) {
        Picasso.with(mContext)
                .load(String.format(Locale.getDefault(), YOUTUBE_THUMBNAIL_TEMPLATE, getContents().get(position).getKey()))
                .into(holder.videoThumbnail);
        if (getContents().get(position).getSite().equalsIgnoreCase(YOUTUBE_VIDEO_SOURCE))
            holder.itemView.setOnClickListener(v -> clickListener.onVideoClicked(getContents().get(position)));
    }

    @Override
    String getEmptyLayoutMessage() {
        return mContext.getString(R.string.video_list_empty_message);
    }
}
