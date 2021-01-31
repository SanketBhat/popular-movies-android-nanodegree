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

package com.udacity.sanketbhat.popularmovies.model;

public class SortOrder {
    public static final int SORT_ORDER_POPULAR = 1;
    public static final int SORT_ORDER_TOP_RATED = 2;

    public static final int SORT_ORDER_DEFAULT = SORT_ORDER_POPULAR;

    private static final String URL_PATH_POPULAR = "popular";
    private static final String URL_PATH_TOP_RATED = "top_rated";

    public static String getSortOrderPath(int sortOrder) {
        switch (sortOrder) {
            case SORT_ORDER_POPULAR:
                return URL_PATH_POPULAR;

            case SORT_ORDER_TOP_RATED:
                return URL_PATH_TOP_RATED;

            default:
                return URL_PATH_POPULAR;
        }
    }
}
