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
package com.udacity.sanketbhat.popularmovies.util

import android.net.Uri

object ImageUrlBuilder {
    private const val SCHEME = "https"
    private const val AUTHORITY_IMAGE = "image.tmdb.org"
    private const val IMAGE_DEFAULT_PATH = "t/p"
    private const val POSTER_IMAGE = 1
    private const val BACKDROP_IMAGE = 2
    private fun getImageUrl(path: String?, imageType: Int): String {
        val builder = Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY_IMAGE)
                .appendEncodedPath(IMAGE_DEFAULT_PATH)
                .appendEncodedPath(getConfigurationString(imageType))
                .appendEncodedPath(path)
        return builder.toString()
    }

    @kotlin.jvm.JvmStatic
    fun getPosterUrlString(path: String?): String {
        return getImageUrl(path, POSTER_IMAGE)
    }

    @kotlin.jvm.JvmStatic
    fun getBackdropUrlString(path: String?): String {
        return getImageUrl(path, BACKDROP_IMAGE)
    }

    private fun getConfigurationString(imageType: Int): String {
        return when (imageType) {
            POSTER_IMAGE -> "w342"
            BACKDROP_IMAGE -> "w780"
            else -> "w185"
        }
    }
}