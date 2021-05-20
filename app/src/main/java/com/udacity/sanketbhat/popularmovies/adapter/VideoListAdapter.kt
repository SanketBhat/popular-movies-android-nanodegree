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
import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.udacity.sanketbhat.popularmovies.R
import com.udacity.sanketbhat.popularmovies.model.Video
import java.util.*

class VideoListAdapter(private val mContext: Context, private val clickListener: VideoClickListener) : RecyclerViewAdapterTemplate<VideoListViewHolder, Video?>(mContext) {
    override fun getNormalViewHolder(parent: ViewGroup): VideoListViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.movie_video_item, parent, false)
        return VideoListViewHolder(rootView)
    }

    override fun normalBinding(holder: VideoListViewHolder, position: Int) {
        var video = contents!![position] as Video
        Picasso.get()
                .load(String.format(Locale.getDefault(), YOUTUBE_THUMBNAIL_TEMPLATE, video.key))
                .into(holder.videoThumbnail)
        if (video.site.equals(YOUTUBE_VIDEO_SOURCE, ignoreCase = true))
            holder.itemView.setOnClickListener { clickListener.onVideoClicked(video) }
    }

    override val emptyLayoutMessage: String
        get() = mContext.getString(R.string.video_list_empty_message)

    companion object {
        private const val YOUTUBE_THUMBNAIL_TEMPLATE = "https://img.youtube.com/vi/%s/mqdefault.jpg"
        private const val YOUTUBE_VIDEO_SOURCE = "youtube"
    }
}