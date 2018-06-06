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

package com.udacity.sanketbhat.popularmovies.util;

import android.net.Uri;

import com.udacity.sanketbhat.popularmovies.model.SortOrder;

public class MovieUrlBuilder {
    //TODO: Insert your API Key
    private static final String API_KEY = "<Your API Key Here>";
    private static final String KEY_API_KEY = "api_key";
    private static final String SCHEME = "https";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String DEFAULT_PATH = "/3/movie/";
    private static final String QUERY_PAGE = "page";
    private static final String PATH_POPULAR_MOVIES = "popular";
    private static final String PATH_TOP_RATED_MOVIES = "top_rated";

    private static final String AUTHORITY_IMAGE = "image.tmdb.org";
    private static final String IMAGE_DEFAULT_PATH = "t/p";

    private static final int POSTER_IMAGE = 1;
    private static final int BACKDROP_IMAGE = 2;

    public static String getUriString(int pageNumber, int sortOrder) {
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendEncodedPath(DEFAULT_PATH);

        if (sortOrder == SortOrder.SORT_ORDER_TOP_RATED)
            uriBuilder.appendPath(PATH_TOP_RATED_MOVIES);
        else
            uriBuilder.appendPath(PATH_POPULAR_MOVIES);

        uriBuilder.appendQueryParameter(KEY_API_KEY, API_KEY);

        if (pageNumber != 0)
            uriBuilder.appendQueryParameter(QUERY_PAGE, String.valueOf(pageNumber));

        return uriBuilder.toString();
    }


    private static String getImageUrl(String path, int imageType) {
        Uri.Builder builder = new Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY_IMAGE)
                .appendEncodedPath(IMAGE_DEFAULT_PATH)
                .appendEncodedPath(getConfigurationString(imageType))
                .appendEncodedPath(path);
        return builder.toString();
    }

    public static String getPosterUrlString(String path) {
        return getImageUrl(path, POSTER_IMAGE);
    }

    public static String getBackdropUrlString(String path) {
        return getImageUrl(path, BACKDROP_IMAGE);
    }

    private static String getConfigurationString(int imageType) {
        switch (imageType) {
            case POSTER_IMAGE:
                return "w342";

            case BACKDROP_IMAGE:
                return "w780";

            default:
                return "w185";
        }
    }
}
