package com.soulquote.app.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.soulquote.app.domain.model.Quote
import com.soulquote.app.presentation.components.QuoteCard
import com.soulquote.app.presentation.components.QuoteDetailDialog
import com.soulquote.app.presentation.home.shareQuote
import com.soulquote.app.presentation.quotes.QuoteViewModel

@Composable
fun FavoritesScreen(
    viewModel: QuoteViewModel,
    modifier: Modifier = Modifier,
    onNavigateToStudio: ((Quote) -> Unit)? = null
) {
    val favorites by viewModel.favoriteQuotes.collectAsState()
    val selectedQuoteForDetail by viewModel.selectedQuoteForDetail.collectAsState()
    val context = LocalContext.current

    selectedQuoteForDetail?.let { quote ->
        QuoteDetailDialog(
            quote = quote,
            onDismiss = { viewModel.showQuoteDetail(null) },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onShare = { shareQuote(context, it) },
            onCustomizeInStudio = onNavigateToStudio
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Saved Reflections",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Quotes that have resonated with your heart.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No saved quotes yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap the heart icon on any quote in Home or Explore to save it here for mindful reflection.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favorites, key = { it.id }) { quote ->
                    QuoteCard(
                        quote = quote,
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onShare = { shareQuote(context, it) },
                        onClick = { viewModel.showQuoteDetail(quote) },
                        onCustomizeInStudio = onNavigateToStudio
                    )
                }
            }
        }
    }
}