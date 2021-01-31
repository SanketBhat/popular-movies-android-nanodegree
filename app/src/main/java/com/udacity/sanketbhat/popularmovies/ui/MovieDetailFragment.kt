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
package com.udacity.sanketbhat.popularmovies.ui

import android.animation.LayoutTransition
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import com.udacity.sanketbhat.popularmovies.R
import com.udacity.sanketbhat.popularmovies.adapter.ReviewListAdapter
import com.udacity.sanketbhat.popularmovies.adapter.VideoClickListener
import com.udacity.sanketbhat.popularmovies.adapter.VideoListAdapter
import com.udacity.sanketbhat.popularmovies.databinding.ActivityMovieDetailContentBinding
import com.udacity.sanketbhat.popularmovies.model.Movie
import com.udacity.sanketbhat.popularmovies.model.ReviewResponse
import com.udacity.sanketbhat.popularmovies.model.Video
import com.udacity.sanketbhat.popularmovies.model.VideoResponse
import com.udacity.sanketbhat.popularmovies.util.ImageUrlBuilder.getPosterUrlString
import java.util.*

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a [MovieListActivity]
 * in two-pane mode (on tablets) or a [MovieDetailActivity]
 * on handsets.
 */
class MovieDetailFragment
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
    : Fragment(), VideoClickListener {
    private var movie: Movie? = null
    private lateinit var viewModel: MovieDetailViewModel
    private var isFavorite = false
    private var optionMenu: Menu? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (arguments != null && arguments!!.containsKey(ARG_ITEM)) {
            movie = arguments!!.getParcelable(ARG_ITEM)
        }
        viewModel = ViewModelProvider(this).get(MovieDetailViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.activity_movie_detail_content, container, false)
        val mBinding: ActivityMovieDetailContentBinding? = DataBindingUtil.bind(rootView)
        rootView.viewTreeObserver.addOnPreDrawListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && activity != null && activity!!.javaClass == MovieDetailActivity::class.java) {
                //Start shared transition on Lollipop and higher devices.
                activity!!.startPostponedEnterTransition()
            }
            true
        }
        (rootView as ConstraintLayout).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        if (movie != null && mBinding != null && context != null) {
            //Loading views with data
            Picasso.with(context)
                    .load(getPosterUrlString(movie!!.posterPath))
                    .error(R.drawable.ic_movie_grid_item_image_error)
                    .into(mBinding.movieDetailPoster)
            mBinding.movie = movie
            mBinding.buttonFavorites.setOnClickListener { if (isFavorite) viewModel.removeFromFavorites(movie) else viewModel.insertIntoFavorites(movie) }
            val videoListAdapter = VideoListAdapter(context!!, this)
            videoListAdapter.setShowLoading(true)
            mBinding.videosList.adapter = videoListAdapter
            mBinding.videosList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val reviewListAdapter = ReviewListAdapter(context)
            reviewListAdapter.setShowLoading(true)
            mBinding.reviewsList.adapter = reviewListAdapter
            mBinding.reviewsList.isNestedScrollingEnabled = false
            mBinding.reviewsList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            viewModel.isFavorite(movie!!.id).observe(viewLifecycleOwner, { movie1: Movie? ->
                isFavorite = movie1 != null
                if (isFavorite) mBinding.buttonFavorites.setImageResource(R.drawable.ic_favorite_white) else mBinding.buttonFavorites.setImageResource(R.drawable.ic_favorite_border_white)
            })
            viewModel.getReviewResponse(movie!!.id).observe(viewLifecycleOwner, { reviewResponse: ReviewResponse? ->
                reviewListAdapter.setContentList(reviewResponse?.reviews)
                if (reviewResponse != null) movie!!.reviewResponse = reviewResponse
            })
            viewModel.getVideoResponse(movie!!.id).observe(viewLifecycleOwner, { videoResponse: VideoResponse? ->
                videoListAdapter.setContentList(videoResponse?.videos)
                if (videoResponse != null) movie!!.videoResponse = videoResponse
                setupShareButton()
            })
            mBinding.cardViewShowMore.setOnClickListener { v: View? ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(THE_MOVIE_DB_WEB_TEMPLATE + movie!!.id))
                if (context!!.packageManager.resolveActivity(intent, 0) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(context, getString(R.string.item_clicked_no_app_error), Toast.LENGTH_SHORT).show()
                }
            }
        } else Log.i(this.javaClass.simpleName, "Can't bind data. movie/context is null!")
        return rootView
    }

    private fun setupShareButton() {
        val videos = movie!!.videos
        if (videos != null && videos.isNotEmpty() && videos[0]!!.site.equals("youtube", ignoreCase = true)) {
            //Enable sharing for first trailer
            if (optionMenu != null && optionMenu!!.findItem(R.id.action_share) != null) optionMenu!!.findItem(R.id.action_share).isVisible = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_detail, menu)
        optionMenu = menu
        setupShareButton()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_share) {
            if (activity != null && movie!!.videos != null && movie!!.videos!!.isNotEmpty() && movie!!.videos!![0]!!.site.equals("youtube", ignoreCase = true)) {
                val url = YOUTUBE_LINK_TEMPLATE + movie!!.videos!![0]!!.key
                val textToShare = String.format(Locale.getDefault(), getString(R.string.share_text_template), url)
                ShareCompat.IntentBuilder.from(activity!!)
                        .setText(textToShare)
                        .setChooserTitle(R.string.intent_chooser_title)
                        .setType("text/plain")
                        .startChooser()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onVideoClicked(video: Video) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_LINK_TEMPLATE + video.key))
        if (context != null && context!!.packageManager.resolveActivity(intent, 0) != null) {
            startActivity(intent)
            return
        }
        Toast.makeText(context, R.string.item_clicked_no_app_error, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ARG_ITEM = "movieItem"
        private const val YOUTUBE_LINK_TEMPLATE = "https://www.youtube.com/watch?v="
        private const val THE_MOVIE_DB_WEB_TEMPLATE = "https://www.themoviedb.org/movie/"
    }
}