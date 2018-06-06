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

import com.udacity.sanketbhat.popularmovies.model.Movie;
import com.udacity.sanketbhat.popularmovies.model.PageResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class JsonUtils {
    private static final String KEY_PAGE = "page";
    private static final String KEY_TOTAL_RESULTS = "total_results";
    private static final String KEY_TOTAL_PAGES = "total_pages";
    private static final String KEY_RESULTS = "results";

    private static final String KEY_ID = "id";
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_POPULARITY = "popularity";
    private static final String KEY_TITLE = "title";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_GENRE_IDS = "genre_ids";
    private static final String KEY_ADULT = "adult";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_RELEASE_DATE = "release_date";

    public static PageResponse getResponse(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int currentPage = jsonObject.optInt(KEY_PAGE);
            int totalPage = jsonObject.optInt(KEY_TOTAL_PAGES);
            int totalResults = jsonObject.optInt(KEY_TOTAL_RESULTS);
            PageResponse pageResponse = new PageResponse(currentPage, totalPage, totalResults);

            JSONArray array = jsonObject.optJSONArray(KEY_RESULTS);
            if (array != null) {
                pageResponse.setMovies(getMovieList(array));
                return pageResponse;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static List<Movie> getMovieList(JSONArray array) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            int id = jsonObject.getInt(KEY_ID);
            int voteCount = jsonObject.getInt(KEY_VOTE_COUNT);
            double voteAverage = jsonObject.getDouble(KEY_VOTE_AVERAGE);
            double popularity = jsonObject.getDouble(KEY_POPULARITY);
            String title = jsonObject.getString(KEY_TITLE);
            String posterPath = jsonObject.getString(KEY_POSTER_PATH);
            String backdropPath = jsonObject.getString(KEY_BACKDROP_PATH);
            int[] genreIds = getGenreIds(jsonObject.getJSONArray(KEY_GENRE_IDS));
            String overview = jsonObject.getString(KEY_OVERVIEW);
            String releaseDate = jsonObject.getString(KEY_RELEASE_DATE);

            movies.add(new Movie(
                    id,
                    voteCount,
                    voteAverage,
                    title,
                    popularity,
                    posterPath,
                    genreIds,
                    backdropPath,
                    overview,
                    releaseDate
            ));
        }
        return movies;
    }

    private static int[] getGenreIds(JSONArray jsonArray) {
        if (jsonArray == null) {
            return new int[0];
        } else {
            int[] genreIds = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                genreIds[i] = jsonArray.optInt(i);
            }
            return genreIds;
        }
    }
}
