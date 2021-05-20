package com.udacity.sanketbhat.popularmovies.ui.views

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.udacity.sanketbhat.popularmovies.R
import com.udacity.sanketbhat.popularmovies.model.Movie
import com.udacity.sanketbhat.popularmovies.ui.LazyPagingGridItems
import com.udacity.sanketbhat.popularmovies.ui.MovieListViewModel
import com.udacity.sanketbhat.popularmovies.ui.collectAsLazyPagingGridItems
import com.udacity.sanketbhat.popularmovies.ui.items
import com.udacity.sanketbhat.popularmovies.util.ImageUrlBuilder
import dev.chrisbanes.accompanist.picasso.PicassoImage
import kotlinx.coroutines.flow.Flow


@Composable
fun MovieItem(movie: Movie, onClick: (Movie) -> Unit) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(4.dp)
    ) {
        Column(modifier = Modifier.clickable { onClick(movie) }) {
            PicassoImage(
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
fun MovieGrid(
    lazyItems: LazyPagingGridItems<Movie>,
    state: LazyListState,
    onClick: (Movie) -> Unit
) {
    LazyVerticalGrid(
        cells = GridCells.Adaptive(160.dp),
        contentPadding = PaddingValues(4.dp),
        state = state
    ) {
        items(lazyItems) {
            MovieItem(movie = it!!, onClick = onClick)
        }
    }
}

@Preview(showSystemUi = false, showBackground = true, widthDp = 50, heightDp = 50)
@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colors.secondary)
    }
}

@Composable
fun ErrorScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_movie_list_error),
            contentDescription = stringResource(id = R.string.movie_list_error_layout_image_description),
            modifier = Modifier
                .height(dimensionResource(id = R.dimen.movie_list_error_layout_image_height))
                .width(dimensionResource(id = R.dimen.movie_list_error_layout_image_width))
        )
        Text(
            text = stringResource(id = R.string.movie_list_error_layout_error_message),
            modifier = Modifier.padding(dimensionResource(id = R.dimen.common_content_margin))
        )
        Button(
            onClick = { onRetry.invoke() },
            colors = buttonColors(backgroundColor = MaterialTheme.colors.secondary)
        ) {
            Text(
                text = stringResource(id = R.string.movie_list_error_layout_retry_button_label),
                style = MaterialTheme.typography.button
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 100)
@Composable
fun EmptyFavoritesScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(id = R.string.movie_list_favorites_empty_message))
    }
}

@Composable
fun CreditsDialog(onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            Button(onClick = onClose, colors = buttonColors(MaterialTheme.colors.secondary)) {
                Text(
                    text = "OK",
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onSecondary
                )
            }
        },
        title = { Text(text = stringResource(id = R.string.menu_main_credits)) },
        text = { Text(text = stringResource(id = R.string.credits_string)) })
}

@ExperimentalAnimationApi
@Composable
fun PopMoviesAppBar(
    selectedScreen: AppScreen,
    onShowCredits: () -> Unit
) {
    PopMoviesTheme {
        TopAppBar(
            title = { Text(text = selectedScreen.title) },
            actions = {
                IconButton(onClick = onShowCredits) {
                    Icon(Icons.Default.List, contentDescription = "Credits")
                }
            }
        )
    }
}

@Composable
fun AppBottomNavigation(
    screens: List<AppScreen>,
    selectedScreen: AppScreen,
    onSelectionChange: (AppScreen) -> Unit
) {
    BottomNavigation {
        screens.forEach { item ->
            BottomNavigationItem(
                selected = item == selectedScreen,
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.title) },
                onClick = { onSelectionChange(item) }
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun MasterView(
    currentScreen: AppScreen,
    itemClickCallback: (Movie) -> Unit
) {
    val lazyItems = currentScreen.dataFlowProvider().collectAsLazyPagingGridItems()
    when (lazyItems.loadState.refresh) {
        is LoadState.Loading -> {
            LoadingScreen()
        }
        is LoadState.Error -> {
            ErrorScreen(onRetry = { lazyItems.retry() })
        }
        else -> {
            MovieGrid(
                state = currentScreen.lazyListState,
                lazyItems = lazyItems,
                onClick = itemClickCallback
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun TwoPaneLayout(
    currentScreen: AppScreen,
    itemClickCallback: (Movie) -> Unit
) {
    Row {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .fillMaxHeight()
        ) {
            MasterView(
                currentScreen = currentScreen,
                itemClickCallback = itemClickCallback
            )
        }
        Column(modifier = Modifier.fillMaxSize()) {
            DetailView()
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun SinglePaneLayout(
    currentScreen: AppScreen,
    itemClickCallback: (Movie) -> Unit
) {
    MasterView(
        currentScreen = currentScreen,
        itemClickCallback = itemClickCallback
    )
}

@ExperimentalAnimationApi
@Composable
fun AppScaffold(
    currentScreen: AppScreen,
    screens: List<AppScreen>,
    viewModel: MovieListViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            PopMoviesAppBar(
                selectedScreen = currentScreen,
                onShowCredits = {
                    viewModel.showCreditsDialog.value = true
                })
        },
        bottomBar = {
            AppBottomNavigation(screens, currentScreen) {
                viewModel.setCurrentScreen(it)
            }
        },
        content = content
    )
}

@Composable
fun DetailView() {

}

data class AppScreen(
    val title: String,
    val icon: ImageVector,
    val dataFlowProvider: () -> Flow<PagingData<Movie>>,
    val lazyListState: LazyListState = LazyListState()
)