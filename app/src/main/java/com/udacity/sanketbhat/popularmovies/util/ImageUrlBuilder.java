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

public class ImageUrlBuilder {
    private static final String SCHEME = "https";
    private static final String AUTHORITY_IMAGE = "image.tmdb.org";
    private static final String IMAGE_DEFAULT_PATH = "t/p";

    private static final int POSTER_IMAGE = 1;
    private static final int BACKDROP_IMAGE = 2;

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
