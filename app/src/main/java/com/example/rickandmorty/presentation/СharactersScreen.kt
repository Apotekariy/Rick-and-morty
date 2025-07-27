package com.example.rickandmorty.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.example.rickandmorty.R
import com.example.rickandmorty.domain.CharacterFromShow
import com.example.rickandmorty.navigation.Screen
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    navController: NavController,
    viewModel: CharactersViewModel = hiltViewModel()
) {
    val characters = viewModel.characters.collectAsLazyPagingItems()
    var showFilterDialog by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val filterOptions by viewModel.filterOptions.collectAsState()
    val isOfflineMode by viewModel.isOfflineMode.collectAsState()

    val isRefreshing = characters.loadState.refresh is LoadState.Loading
    val pullRefreshState = rememberPullToRefreshState()

    val isLoading = characters.loadState.refresh is LoadState.Loading
    val showEmptyState = !isLoading && characters.itemCount == 0
    val hasFilters = filterOptions.hasFilters()

    // Поиск с задержкой
    LaunchedEffect(searchQuery) {
        delay(500) // Задержка для избежания частых запросов
        if (searchQuery.isNotBlank() || filterOptions.name != searchQuery) {
            viewModel.applyFilter(
                filterOptions.copy(name = searchQuery.takeIf { it.isNotBlank() })
            )
        }
    }

    // Проверка на ошибки сети для установки офлайн режима
    LaunchedEffect(characters.loadState.refresh) {
        when (val refreshState = characters.loadState.refresh) {
            is LoadState.Error -> {
                val isNetworkError = refreshState.error is java.net.UnknownHostException ||
                        refreshState.error is java.net.SocketTimeoutException ||
                        refreshState.error is java.io.IOException
                if (isNetworkError) {
                    viewModel.setOfflineMode(true)
                }
            }
            is LoadState.NotLoading -> {
                if (characters.itemCount > 0) {
                    viewModel.setOfflineMode(false)
                }
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            if (showSearchBar) {
                SearchTopAppBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onCloseSearch = {
                        showSearchBar = false
                        searchQuery = ""
                        viewModel.applyFilter(filterOptions.copy(name = null))
                    },
                    placeholder = "Search characters..."
                )
            } else {
                TopAppBar(
                    title = {
                        Column {
                            Text("Rick and Morty Characters")
                            if (isOfflineMode) {
                                Text(
                                    "Offline Mode",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSearchBar = true }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }

                        IconButton(onClick = { characters.refresh() }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                        }

                        IconButton(onClick = { showFilterDialog = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_filter_list_24),
                                contentDescription = "Filters",
                                tint = if (hasFilters) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (hasFilters) {
                            IconButton(onClick = {
                                viewModel.clearFilters()
                                searchQuery = ""
                            }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear filters")
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            state = pullRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = { characters.refresh() },
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            CharacterContent(
                characters = characters,
                isLoading = isLoading,
                showEmptyState = showEmptyState,
                hasFilters = hasFilters || searchQuery.isNotBlank(),
                searchQuery = searchQuery,
                onCharacterClick = { id ->
                    navController.navigate(Screen.CharacterDetails.createRoute(id))
                }
            )
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            currentFilter = filterOptions,
            onApply = { newFilter ->
                viewModel.applyFilter(newFilter)
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    placeholder: String = "Search..."
) {
    TopAppBar(
        title = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text(placeholder) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onCloseSearch) {
                Icon(Icons.Filled.Close, contentDescription = "Close search")
            }
        },
        actions = {
            if (searchQuery.isNotBlank()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                }
            }
        }
    )
}

@Composable
fun CharacterContent(
    characters: LazyPagingItems<CharacterFromShow>,
    isLoading: Boolean,
    showEmptyState: Boolean,
    hasFilters: Boolean,
    searchQuery: String = "",
    onCharacterClick: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            showEmptyState -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = when {
                            searchQuery.isNotBlank() -> "No characters found for '$searchQuery'"
                            hasFilters -> "No characters found with current filters"
                            else -> "No characters found"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (hasFilters || searchQuery.isNotBlank()) {
                        Text(
                            text = "Try adjusting your search criteria",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            characters.itemCount > 0 -> {
                CharacterGrid(
                    characters = characters,
                    onCharacterClick = onCharacterClick
                )
            }
        }

        if (isLoading && characters.itemCount == 0) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }

        if (characters.loadState.append is LoadState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }

        // Показываем ошибку, если есть
        if (characters.loadState.refresh is LoadState.Error && characters.itemCount == 0) {
            val error = (characters.loadState.refresh as LoadState.Error).error
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Failed to load characters",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = when (error) {
                        is java.net.UnknownHostException -> "No internet connection"
                        is java.net.SocketTimeoutException -> "Connection timeout"
                        is java.io.IOException -> "Network error occurred"
                        else -> "Check your internet connection"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { characters.retry() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun CharacterGrid(
    characters: LazyPagingItems<CharacterFromShow>,
    onCharacterClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(characters.itemCount) { index ->
            characters[index]?.let { character ->
                EnhancedCharacterItem(
                    character = character,
                    onClick = { onCharacterClick(character.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedCharacterItem(
    character: CharacterFromShow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = character.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = character.species,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = character.status,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (character.status.lowercase()) {
                        "alive" -> MaterialTheme.colorScheme.primary
                        "dead" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Text(
                    text = character.gender,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}