package com.soulquote.app.presentation.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soulquote.app.core.localization.LocalAppStrings
import com.soulquote.app.domain.model.MoodType
import com.soulquote.app.domain.model.Quote
import com.soulquote.app.domain.model.QuoteCategory
import com.soulquote.app.presentation.components.QuoteCard
import com.soulquote.app.presentation.components.QuoteDetailDialog
import com.soulquote.app.presentation.journal.JournalViewModel
import com.soulquote.app.presentation.journal.MindfulBadgesDialog
import com.soulquote.app.presentation.journal.MindfulJournalDialog
import com.soulquote.app.presentation.quotes.QuoteViewModel
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: QuoteViewModel,
    onNavigateToCategory: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToStudio: ((Quote) -> Unit)? = null,
    onNavigateToMeditation: (() -> Unit)? = null,
    onNavigateToAmbient: (() -> Unit)? = null,
    onNavigateToFavorites: (() -> Unit)? = null,
    journalViewModel: JournalViewModel? = null
) {
    val strings = LocalAppStrings.current
    val dailyQuote by viewModel.dailyQuote.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedQuoteForDetail by viewModel.selectedQuoteForDetail.collectAsState()
    val context = LocalContext.current

    var showJournalDialog by remember { mutableStateOf(false) }
    var showBadgesDialog by remember { mutableStateOf(false) }

    val streakInfo = journalViewModel?.streakInfo?.collectAsStateWithLifecycle()?.value
    val todayEntries = journalViewModel?.todayEntries?.collectAsStateWithLifecycle()?.value ?: emptyList()
    val todayEntry = todayEntries.firstOrNull()
    val currentPrompt = journalViewModel?.currentPrompt?.collectAsStateWithLifecycle()?.value
        ?: strings.defaultJournalPrompt
    val selectedMood = journalViewModel?.selectedMood?.collectAsStateWithLifecycle()?.value ?: MoodType.CALM

    val greeting = when (LocalTime.now().hour) {
        in 5..11 -> strings.greetingMorning
        in 12..16 -> strings.greetingAfternoon
        in 17..21 -> strings.greetingEvening
        else -> strings.greetingNight
    }

    selectedQuoteForDetail?.let { quote ->
        QuoteDetailDialog(
            quote = quote,
            onDismiss = { viewModel.showQuoteDetail(null) },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onShare = { shareQuote(context, it, strings.shareQuoteTitle) },
            onCustomizeInStudio = onNavigateToStudio
        )
    }

    if (showJournalDialog && journalViewModel != null) {
        MindfulJournalDialog(
            viewModel = journalViewModel,
            onDismiss = { showJournalDialog = false },
            associatedQuoteId = dailyQuote?.id
        )
    }

    if (showBadgesDialog && journalViewModel != null) {
        MindfulBadgesDialog(
            viewModel = journalViewModel,
            onDismiss = { showBadgesDialog = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Mindful Greeting Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.todaysReflection,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Mindful Streak Badge Pill
                    if (journalViewModel != null) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                                .clickable { showBadgesDialog = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔥", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${streakInfo?.currentStreak ?: 0} ${strings.daysUnit}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    IconButton(onClick = { viewModel.loadDailyQuote() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Daily Quote",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (onNavigateToFavorites != null) {
                        IconButton(onClick = onNavigateToFavorites) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = strings.navFavorites,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Featured Daily Quote Card
        item {
            dailyQuote?.let { quote ->
                QuoteCard(
                    quote = quote,
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onShare = { shareQuote(context, it, strings.shareQuoteTitle) },
                    onClick = { viewModel.showQuoteDetail(quote) },
                    isFeatured = true,
                    onCustomizeInStudio = onNavigateToStudio
                )
            } ?: run {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = strings.loadingDailyQuote,
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Mindful Mood Check-in & Journal Card (Phase 10)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🧘", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.innerReflectionMood,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        if (todayEntries.isNotEmpty()) {
                            val countText = if (todayEntries.size > 1) " (${todayEntries.size})" else ""
                            Text(
                                text = "${todayEntries.first().mood.emoji} ${strings.savedBadge}$countText",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentPrompt,
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Mood Selectors
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MoodType.entries.forEach { mood ->
                            val isSelected = mood == selectedMood
                            Box(
                                modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                    else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    journalViewModel?.selectMood(mood)
                                    showJournalDialog = true
                                },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = mood.emoji, fontSize = 16.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { showJournalDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (todayEntries.isNotEmpty()) strings.openJournalButton else strings.writeReflection,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }

        // Browse by Themes / Categories Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.themesOfWisdom,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    ElevatedFilterChip(
                        selected = false,
                        onClick = { onNavigateToCategory(category.id) },
                        label = { Text(category.name) },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }

        // Guided Meditation shortcut card
        if (onNavigateToMeditation != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToMeditation() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.guidedMeditationTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = strings.guidedMeditationDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = strings.playLabel,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Ambient Soundscape shortcut card
        if (onNavigateToAmbient != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAmbient() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Waves,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.ambientSoundscapesTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = strings.ambientSoundscapesDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = strings.playLabel,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

fun shareQuote(context: android.content.Context, quote: Quote, chooserTitle: String = "Bagikan Kutipan") {
    val shareText = "\"${quote.text}\"\n\n— ${quote.author}\n\nShared via SoulQuote"
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, chooserTitle)
    context.startActivity(shareIntent)
}