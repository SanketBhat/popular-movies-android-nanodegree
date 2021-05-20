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
package com.udacity.sanketbhat.popularmovies.model

object SortOrder {
    const val SORT_ORDER_POPULAR = 1
    const val SORT_ORDER_TOP_RATED = 2
    const val SORT_ORDER_DEFAULT = SORT_ORDER_POPULAR
    const val URL_PATH_POPULAR = "popular"
    const val URL_PATH_TOP_RATED = "top_rated"

    @kotlin.jvm.JvmStatic
    fun getSortOrderPath(sortOrder: Int): String {
        return when (sortOrder) {
            SORT_ORDER_POPULAR -> URL_PATH_POPULAR
            SORT_ORDER_TOP_RATED -> URL_PATH_TOP_RATED
            else -> URL_PATH_POPULAR
        }
    }
}