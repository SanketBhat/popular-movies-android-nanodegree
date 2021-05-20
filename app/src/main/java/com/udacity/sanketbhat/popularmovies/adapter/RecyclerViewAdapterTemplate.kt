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
import androidx.recyclerview.widget.RecyclerView
import com.udacity.sanketbhat.popularmovies.R

/**
 * @param <VH> ViewHolder for normal item
 * @param <T>  List item to show
 * @author Sanket Bhat
 * This class is a RecyclerView.Adapter Template that has both progress and error/empty layouts.
 *
 *
 *
 * The class has setter and getter for list of objects. The *setShowLoading(boolean showLoading)*
 * method shows or hides the progressbar. and setting passing null to setContents() will show the empty/error layout
</T></VH> */
abstract class RecyclerViewAdapterTemplate<VH : RecyclerView.ViewHolder?, T> internal constructor(val context: Context?) : RecyclerView.Adapter<VH>() {
    /**
     * Method for getting content list
     *
     * @return returns the list of object of type `T`
     */
    //List of contents to show
    var contents: List<T>? = null
        private set
    private var showLoading = false
    private var showEmpty = false
    override fun getItemViewType(position: Int): Int {
        return if (showLoading) VIEW_TYPE_PROGRESS else if (showEmpty) VIEW_TYPE_EMPTY else VIEW_TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_NORMAL -> getNormalViewHolder(parent)
            VIEW_TYPE_PROGRESS -> {
                val rootView = inflater.inflate(R.layout.progress_bar, parent, false)
                ProgressViewHolder(rootView) as VH
            }
            VIEW_TYPE_EMPTY -> {
                val rootView = inflater.inflate(R.layout.empty_layout, parent, false)
                EmptyViewHolder(rootView, emptyLayoutMessage) as VH
            }
            else -> throw IllegalArgumentException("Unsupported view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_NORMAL) {
            try {
                normalBinding(
                    holder, position
                )
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (showEmpty || showLoading) 1 else contents?.size ?: 0
    }

    fun setContentList(contentList: List<T>?) {
        showLoading = false
        if (contentList != null && contentList.isNotEmpty()) {
            showEmpty = false
            contents = contentList
        } else {
            showEmpty = true
        }
        notifyDataSetChanged()
    }

    fun setShowLoading(showLoading: Boolean) {
        this.showLoading = showLoading
        notifyDataSetChanged()
    }

    /**
     * Method for implementing custom view holder for normal scenario
     * it should return the view holder of type VH, that should be inflated and
     * constructed with normal view that should be shown to bind the data.
     *
     *
     * Called when content list one or more items
     *
     * @param parent View Group parent item, the view should be inflated for this
     * @return Normal View Holder to show list of contents
     */
    abstract fun getNormalViewHolder(parent: ViewGroup): VH

    /**
     * This method should bind the data with the views
     * viewHolder of type VH. The data object can be accessed using
     * position and function getContents()
     * @param holder viewHolder of type VH
     * @param position position of the viewHolder in the list
     */
    abstract fun normalBinding(holder: VH, position: Int)

    /**
     * @return It should return String message that should be shown when no items
     * are present
     */
    abstract val emptyLayoutMessage: String

    companion object {
        //Separate flags for view types
        private const val VIEW_TYPE_NORMAL = 1
        private const val VIEW_TYPE_EMPTY = 2
        private const val VIEW_TYPE_PROGRESS = 4
    }
}