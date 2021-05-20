package com.udacity.sanketbhat.popularmovies.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyGridScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.*
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Note! : This is a convenience class and
 * has most similar interface as [androidx.paging.compose.LazyPagingItems]
 *
 * The class responsible for accessing the data from a [Flow] of [PagingData].
 * In order to obtain an instance of [LazyPagingGridItems] use the [collectAsLazyPagingGridItems] extension
 * method of [Flow] with [PagingData].
 * This instance can be used by the [items] and [itemsIndexed] methods inside [LazyGridScope] to
 * display data received from the [Flow] of [PagingData].
 *
 * @param flow the [Flow] object which contains a stream of [PagingData] elements.
 * @param T the type of value used by [PagingData].
 */
class LazyPagingGridItems<T : Any> internal constructor(
    private val flow: Flow<PagingData<T>>
) {
    /**
     * The number of items which can be accessed.
     */
    val itemCount: Int
        get() = pagingDataDiffer.size

    private val mainDispatcher = Dispatchers.Main

    internal val recomposerPlaceholder: MutableState<Int> = mutableStateOf(0)

    @SuppressLint("RestrictedApi")
    private val differCallback = object : DifferCallback {
        override fun onChanged(position: Int, count: Int) {
            recomposerPlaceholder.value++
        }

        override fun onInserted(position: Int, count: Int) {
            recomposerPlaceholder.value++
        }

        override fun onRemoved(position: Int, count: Int) {
            recomposerPlaceholder.value++
        }
    }

    private val pagingDataDiffer = object : PagingDataDiffer<T>(
        differCallback = differCallback,
        mainDispatcher = mainDispatcher
    ) {
        override suspend fun presentNewList(
            previousList: NullPaddedList<T>,
            newList: NullPaddedList<T>,
            newCombinedLoadStates: CombinedLoadStates,
            lastAccessedIndex: Int,
            onListPresentable: () -> Unit,
        ): Int? {
            onListPresentable()
            recomposerPlaceholder.value++
            return null
        }
    }

    operator fun get(index: Int): T? {
        return pagingDataDiffer[index]
    }

    fun peek(index: Int): T? {
        return pagingDataDiffer.peek(index)
    }

    fun snapshot(): ItemSnapshotList<T> {
        return pagingDataDiffer.snapshot()
    }

    fun retry() {
        pagingDataDiffer.retry()
    }

    fun refresh() {
        pagingDataDiffer.refresh()
    }

    var loadState: CombinedLoadStates by mutableStateOf(
        CombinedLoadStates(
            refresh = InitialLoadStates.refresh,
            prepend = InitialLoadStates.prepend,
            append = InitialLoadStates.append,
            source = InitialLoadStates,
        )
    )
        private set

    internal suspend fun collectLoadState() {
        pagingDataDiffer.loadStateFlow.collect {
            loadState = it
        }
    }

    internal suspend fun collectPagingData() {
        flow.collectLatest {
            pagingDataDiffer.collectFrom(it)
        }
    }
}

private val IncompleteLoadState = LoadState.NotLoading(false)
private val InitialLoadStates = LoadStates(
    IncompleteLoadState,
    IncompleteLoadState,
    IncompleteLoadState
)


@Composable
fun <T : Any> Flow<PagingData<T>>.collectAsLazyPagingGridItems(): LazyPagingGridItems<T> {
    val LazyPagingGridItems = remember(this) { LazyPagingGridItems(this) }

    LaunchedEffect(LazyPagingGridItems) {
        launch { LazyPagingGridItems.collectPagingData() }
        launch { LazyPagingGridItems.collectLoadState() }
    }

    return LazyPagingGridItems
}

@ExperimentalFoundationApi
fun <T : Any> LazyGridScope.items(
    LazyPagingGridItems: LazyPagingGridItems<T>,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    // this state recomposes every time the LazyPagingGridItems receives an update and changes the
    // value of recomposerPlaceholder
    @Suppress("UNUSED_VARIABLE")
    val recomposerPlaceholder = LazyPagingGridItems.recomposerPlaceholder.value

    items(LazyPagingGridItems.itemCount) { index ->
        val item = LazyPagingGridItems[index]
        itemContent(item)
    }
}

@ExperimentalFoundationApi
fun <T : Any> LazyGridScope.itemsIndexed(
    LazyPagingGridItems: LazyPagingGridItems<T>,
    itemContent: @Composable LazyItemScope.(index: Int, value: T?) -> Unit
) {
    // this state recomposes every time the LazyPagingGridItems receives an update and changes the
    // value of recomposerPlaceholder
    @Suppress("UNUSED_VARIABLE")
    val recomposerPlaceholder = LazyPagingGridItems.recomposerPlaceholder.value

    items(LazyPagingGridItems.itemCount) { index ->
        val item = LazyPagingGridItems[index]
        itemContent(index, item)
    }
}
