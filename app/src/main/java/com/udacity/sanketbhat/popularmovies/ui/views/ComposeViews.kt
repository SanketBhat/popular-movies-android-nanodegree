package com.udacity.sanketbhat.popularmovies.ui.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.PagingData
import com.udacity.sanketbhat.popularmovies.model.Movie
import com.udacity.sanketbhat.popularmovies.ui.LazyPagingGridItems
import com.udacity.sanketbhat.popularmovies.ui.collectAsLazyPagingGridItems
import com.udacity.sanketbhat.popularmovies.ui.items
import com.udacity.sanketbhat.popularmovies.util.ImageUrlBuilder
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.flow.Flow


@Composable
fun MovieItem(movie: Movie, onClick: (Movie) -> Unit) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .clickable { onClick(movie) }
            .padding(4.dp)
    ) {
        Column {
            CoilImage(
                data = ImageUrlBuilder.getPosterUrlString(movie.posterPath),
                contentDescription = "Movie Poster",
                modifier = Modifier
                    .aspectRatio(0.8f)
                    .height(160.dp),
                fadeIn = true,
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = movie.title!!,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = movie.genresString,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun MainGrid(
    sortOrder: String?,
    dataProvider: (String?) -> Flow<PagingData<Movie>>,
    onClick: (Movie) -> Unit
) {
    val lazyItems = dataProvider(sortOrder).collectAsLazyPagingGridItems()
    MovieGrid(lazyItems = lazyItems, onClick = onClick)
}

@ExperimentalFoundationApi
@Composable
fun FavoritesGrid(
    dataProvider: () -> Flow<PagingData<Movie>>,
    onClick: (Movie) -> Unit
) {
    val lazyItems = dataProvider().collectAsLazyPagingGridItems()
    MovieGrid(lazyItems = lazyItems, onClick = onClick)
}

@ExperimentalFoundationApi
@Composable
fun MovieGrid(lazyItems: LazyPagingGridItems<Movie>, onClick: (Movie) -> Unit) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(lazyItems) {
            MovieItem(movie = it!!, onClick = onClick)
        }
    }
}