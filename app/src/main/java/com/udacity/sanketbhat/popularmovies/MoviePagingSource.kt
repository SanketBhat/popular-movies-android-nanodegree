package com.udacity.sanketbhat.popularmovies

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.udacity.sanketbhat.popularmovies.api.TheMovieDBApiService
import com.udacity.sanketbhat.popularmovies.model.Movie
const val MOVIE_STARTING_PAGE_INDEX = 1
const val MOVIE_PAGE_SIZE = 20
class MoviePagingSource(
        private val movieDbApi: TheMovieDBApiService,
        private val sortOrder: String
) : PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val position = params.key ?: MOVIE_STARTING_PAGE_INDEX
        return try {
            val response = movieDbApi.getPage(sortOrder, position)
            val movies = response.movies ?: emptyList()
            val nextKey = if (movies.isEmpty() || (response.totalPages != null && position >= response.totalPages!!)) {
                null
            } else {
                position + 1
            }
            LoadResult.Page(
                    data = movies,
                    prevKey = if (position == MOVIE_STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}