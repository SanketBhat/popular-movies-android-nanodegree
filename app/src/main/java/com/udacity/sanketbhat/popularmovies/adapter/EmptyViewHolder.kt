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

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udacity.sanketbhat.popularmovies.R

/**
 * Class for showing empty message when there are no videos or reviews for a movie
 */
internal class EmptyViewHolder(itemView: View, text: String?) : RecyclerView.ViewHolder(itemView) {
    init {
        //Show the empty content message in a textView
        (itemView.findViewById<View>(R.id.empty_text_view) as TextView).text = text
    }
}