package com.udacity.sanketbhat.popularmovies.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.sanketbhat.popularmovies.R
import com.udacity.sanketbhat.popularmovies.adapter.MovieListAdapter.Companion.VIEW_TYPE_PROGRESS

class MovieLoadStateAdapter(
        private val retry: () -> Unit
) : LoadStateAdapter<MovieLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: MovieLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): MovieLoadStateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_loading_indicator, parent, false)
        return MovieLoadStateViewHolder(view, retry)
    }

    override fun getStateViewType(loadState: LoadState): Int {
        return VIEW_TYPE_PROGRESS
    }
}

class MovieLoadStateViewHolder(itemView: View, retry: () -> Unit) : RecyclerView.ViewHolder(itemView){
    private val progressBar: ProgressBar = itemView.findViewById(R.id.footer_progress_bar)
    private val progressText: TextView = itemView.findViewById(R.id.footer_progress_text)
    private val retryButton: Button = itemView.findViewById(R.id.footer_retry_button)
    init {
        retryButton.setOnClickListener { retry.invoke() }
    }
    fun bind(loadState: LoadState){
        progressBar.visibility = if(loadState is LoadState.Loading) View.VISIBLE else View.GONE
        progressText.visibility = progressBar.visibility
        retryButton.visibility = if(loadState !is LoadState.Loading) View.VISIBLE else View.GONE
    }
}