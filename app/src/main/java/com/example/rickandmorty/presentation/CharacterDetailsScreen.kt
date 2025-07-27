package com.example.rickandmorty.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.rickandmorty.domain.CharacterFromShow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(
    characterId: Int,
    navController: NavController,
    viewModel: CharacterDetailsViewModel = hiltViewModel()
) {
    val character by viewModel.character.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Обработка системной кнопки назад
    BackHandler {
        navController.popBackStack()
    }

    LaunchedEffect(characterId) {
        if (characterId != -1) {
            viewModel.loadCharacter(characterId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(character?.name ?: "Character Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            // Дополнительная кнопка назад внизу экрана
            if (character != null || errorMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 8.dp
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Back to Characters")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Failed to load character",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { viewModel.loadCharacter(characterId) }
                        ) {
                            Text("Retry")
                        }
                    }
                }

                character != null -> {
                    CharacterDetailsContent(
                        character = character!!,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    )
                }

                characterId == -1 -> {
                    Text(
                        "Invalid character ID",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    Text(
                        "Character not found",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun CharacterDetailsContent(
    character: CharacterFromShow,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Character Image
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }

        // Character Information
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Character Information",
                    style = MaterialTheme.typography.titleLarge
                )

                DetailItem(title = "Name", value = character.name)
                DetailItem(title = "Status", value = character.status)
                DetailItem(title = "Species", value = character.species)
                DetailItem(title = "Type", value = character.type.ifEmpty { "Unknown" })
                DetailItem(title = "Gender", value = character.gender)
            }
        }

        // Location Information
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Location Information",
                    style = MaterialTheme.typography.titleLarge
                )

                DetailItem(title = "Origin", value = character.origin.name)
                DetailItem(title = "Current Location", value = character.location.name)
            }
        }

        // Episodes
        if (character.episode.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Episodes (${character.episode.size})",
                        style = MaterialTheme.typography.titleLarge
                    )

                    character.episode.take(10).forEach { episode ->
                        Text(
                            text = "• Episode ${episode.substringAfterLast("/")}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (character.episode.size > 10) {
                        Text(
                            text = "... and ${character.episode.size - 10} more episodes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(72.dp))
    }
}

@Composable
fun DetailItem(title: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value.ifEmpty { "Unknown" },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}