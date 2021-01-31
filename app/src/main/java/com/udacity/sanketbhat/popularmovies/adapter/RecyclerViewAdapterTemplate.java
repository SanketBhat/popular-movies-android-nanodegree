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
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.sanketbhat.popularmovies.R;

import java.util.List;

/**
 * @param <VH> ViewHolder for normal item
 * @param <T>  List item to show
 * @author Sanket Bhat
 * This class is a RecyclerView.Adapter Template that has both progress and error/empty layouts.
 * <p>
 * <p>The class has setter and getter for list of objects. The <i>setShowLoading(boolean showLoading)</i>
 * method shows or hides the progressbar. and setting passing null to setContents() will show the empty/error layout</p>
 */
public abstract class RecyclerViewAdapterTemplate<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter {
    //Separate flags for view types
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_EMPTY = 2;
    private static final int VIEW_TYPE_PROGRESS = 4;
    //List of contents to show
    private List<T> contents;
    private boolean showLoading;
    private boolean showEmpty;
    private final Context mContext;

    RecyclerViewAdapterTemplate(Context mContext) {
        this.mContext = mContext;
    }

    Context getContext() {
        return mContext;
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoading) return VIEW_TYPE_PROGRESS;
        else if (showEmpty) return VIEW_TYPE_EMPTY;
        else return VIEW_TYPE_NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return getNormalViewHolder(parent);

            case VIEW_TYPE_PROGRESS:
                View rootView = inflater.inflate(R.layout.progress_bar, parent, false);
                return new ProgressViewHolder(rootView);

            case VIEW_TYPE_EMPTY:
                rootView = inflater.inflate(R.layout.empty_layout, parent, false);
                return new EmptyViewHolder(rootView, getEmptyLayoutMessage());

            default:
                throw new IllegalArgumentException("Unsupported view type: " + viewType);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_NORMAL) {
            try {
                @SuppressWarnings("unchecked") VH viewHolder = (VH) holder; //Handling cast exception.
                normalBinding(viewHolder, position);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public int getItemCount() {
        if (showEmpty || showLoading) return 1;
        else return contents == null ? 0 : contents.size();
    }

    public void setContentList(List<T> contentList) {
        showLoading = false;
        if (contentList != null && contentList.size() > 0) {
            showEmpty = false;
            this.contents = contentList;
        } else {
            showEmpty = true;
        }
        notifyDataSetChanged();
    }

    /**
     * Method for getting content list
     *
     * @return returns the list of object of type <code>T</code>
     */
    List<T> getContents() {
        return contents;
    }

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    /**
     * Method for implementing custom view holder for normal scenario
     * it should return the view holder of type VH, that should be inflated and
     * constructed with normal view that should be shown to bind the data.
     * <p>
     * Called when content list one or more items
     *
     * @param parent View Group parent item, the view should be inflated for this
     * @return Normal View Holder to show list of contents
     */
    abstract VH getNormalViewHolder(ViewGroup parent);

    /**
     * This method should bind the data with the views
     * viewHolder of type VH. The data object can be accessed using
     * position and function getContents()
     * @param holder viewHolder of type VH
     * @param position position of the viewHolder in the list
     */
    abstract void normalBinding(VH holder, int position);

    /**
     * @return It should return String message that should be shown when no items
     * are present
     */
    abstract String getEmptyLayoutMessage();
}
