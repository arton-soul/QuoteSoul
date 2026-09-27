package com.soulquote.app.presentation.meditation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soulquote.app.domain.model.Meditation
import com.soulquote.app.presentation.ambient.AmbientMixerScreen
import com.soulquote.app.presentation.ambient.AmbientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationCatalogScreen(
    viewModel: MeditationViewModel,
    ambientViewModel: AmbientViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedTabIndex by ambientViewModel.selectedTab.collectAsStateWithLifecycle()
    val catalogState by viewModel.catalogUiState.collectAsStateWithLifecycle()
    val playerState by viewModel.playerUiState.collectAsStateWithLifecycle()

    LaunchedEffect(catalogState.statusMessage) {
        catalogState.statusMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column {
                            Text(
                                text = "Meditation",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Keheningan & ketenangan batin",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { ambientViewModel.selectTab(0) },
                        text = { Text("Panduan Suara", fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(Icons.Default.SelfImprovement, contentDescription = null, modifier = Modifier.size(20.dp)) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { ambientViewModel.selectTab(1) },
                        text = { Text("Suara Alam", fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(Icons.Default.Waves, contentDescription = null, modifier = Modifier.size(20.dp)) }
                    )
                }

                if (selectedTabIndex == 0) {
                    // Search Bar
                    OutlinedTextField(
                        value = catalogState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        placeholder = { Text("Cari meditasi atau instruktur...", fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Cari",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (catalogState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Hapus",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Category Chips Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isAllSelected = catalogState.selectedCategoryId == "all"
                        FilterChip(
                            selected = isAllSelected,
                            onClick = { viewModel.selectCategory("all") },
                            label = { Text("Semua", fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )

                        catalogState.categories.forEach { category ->
                            val selected = catalogState.selectedCategoryId == category.id
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.selectCategory(category.id) },
                                label = { Text(category.name, fontSize = 13.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    // Meditation List
                    val filtered = catalogState.filteredMeditations
                    if (filtered.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tidak ada sesi meditasi yang sesuai.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filtered, key = { it.id }) { meditation ->
                                val isCurrentPlaying = playerState.currentMeditation?.id == meditation.id && playerState.isPlaying
                                MeditationSessionCard(
                                    meditation = meditation,
                                    isCurrentPlaying = isCurrentPlaying,
                                    onPlayClick = { viewModel.playMeditation(meditation) },
                                    onDownloadClick = {
                                        if (meditation.isDownloaded) {
                                            viewModel.deleteDownload(meditation)
                                        } else {
                                            viewModel.downloadAudio(meditation)
                                        }
                                    }
                                )
                            }
                        }
                    }
                } else {
                    AmbientMixerScreen(
                        viewModel = ambientViewModel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            // Persistent Floating Mini Player
            AnimatedVisibility(
                visible = playerState.currentMeditation != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                playerState.currentMeditation?.let { med ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.expandPlayer() },
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp
                    ) {
                        Column {
                            LinearProgressIndicator(
                                progress = { playerState.progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SelfImprovement,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = med.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${playerState.formattedCurrentTime} / ${playerState.formattedDurationTime}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    FilledIconButton(
                                        onClick = { viewModel.togglePlayPause() },
                                        modifier = Modifier.size(42.dp),
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (playerState.isPlaying) "Jeda" else "Putar"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Full Player Modal
    if (playerState.isPlayerExpanded && playerState.currentMeditation != null) {
        MeditationPlayerModal(
            playerState = playerState,
            onTogglePlayPause = { viewModel.togglePlayPause() },
            onSeekTo = { viewModel.seekTo(it) },
            onSeekRelative = { viewModel.seekRelative(it) },
            onSetSpeed = { viewModel.setPlaybackSpeed(it) },
            onDownload = { viewModel.downloadAudio(it) },
            onDeleteDownload = { viewModel.deleteDownload(it) },
            onDismiss = { viewModel.collapsePlayer() }
        )
    }
}

@Composable
fun MeditationSessionCard(
    meditation: Meditation,
    isCurrentPlaying: Boolean,
    onPlayClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onPlayClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPlaying) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Duration & Offline Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Duration badge
                    Row(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${meditation.durationSeconds / 60} Menit",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Offline badge
                    if (meditation.isDownloaded) {
                        Row(
                            modifier = Modifier
                                .background(
                                    Color(0xFFE8F5E9),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Color(0xFF2E7D32)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Offline",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = meditation.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Description
                meditation.description?.let { desc ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Instructor
                meditation.instructor?.let { instructor ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Instruktur: $instructor",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Action Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Play / Pause Circle
                FilledIconButton(
                    onClick = onPlayClick,
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isCurrentPlaying) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primaryContainer,
                        contentColor = if (isCurrentPlaying) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = if (isCurrentPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isCurrentPlaying) "Jeda" else "Putar"
                    )
                }

                // Download icon button
                IconButton(
                    onClick = onDownloadClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (meditation.isDownloaded) Icons.Default.CheckCircle else Icons.Default.CloudDownload,
                        contentDescription = if (meditation.isDownloaded) "Tersedia Offline" else "Unduh Offline",
                        tint = if (meditation.isDownloaded) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
