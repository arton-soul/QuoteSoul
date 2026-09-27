package com.soulquote.app.presentation.studio

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soulquote.app.domain.model.Quote

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val availableQuotes by viewModel.availableQuotes.collectAsStateWithLifecycle()

    var showQuotePicker by remember { mutableStateOf(false) }
    var activeTab by remember { mutableIntStateOf(0) }

    // Modern zero-permission Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setBackground(StudioBackground.CustomImage(uri))
        }
    }

    LaunchedEffect(uiState.exportStatusMessage) {
        uiState.exportStatusMessage?.let { msg ->
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
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Quote Studio",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showQuotePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Pick Quote",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.saveToGallery(context) },
                        enabled = !uiState.isExporting
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Save to Gallery"
                        )
                    }
                    IconButton(
                        onClick = { viewModel.shareImage(context) },
                        enabled = !uiState.isExporting
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Image",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Preview Canvas Card (Flexible upper section)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                StudioPreviewCard(uiState = uiState)

                if (uiState.isExporting) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }

            // 2. Control Tabs
            val tabs = listOf("Format", "Theme", "Text", "Effects")
            PrimaryTabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = activeTab == index,
                        onClick = { activeTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp),
                                maxLines = 1,
                                fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // 3. Tab Content Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                when (activeTab) {
                    0 -> FormatControls(
                        currentRatio = uiState.aspectRatio,
                        onSelectRatio = { viewModel.setAspectRatio(it) }
                    )
                    1 -> BackgroundControls(
                        currentBackground = uiState.background,
                        onSelectBackground = { viewModel.setBackground(it) },
                        onPickCustomPhoto = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )
                    2 -> TypographyControls(
                        font = uiState.font,
                        fontSizeSp = uiState.fontSizeSp,
                        textAlign = uiState.textAlign,
                        textColor = uiState.textColor,
                        onSelectFont = { viewModel.setFont(it) },
                        onFontSizeChange = { viewModel.setFontSize(it) },
                        onSelectAlign = { viewModel.setTextAlign(it) },
                        onSelectTextColor = { viewModel.setTextColor(it) }
                    )
                    3 -> EffectsControls(
                        overlayOpacity = uiState.overlayOpacity,
                        showWatermark = uiState.showWatermark,
                        showAuthor = uiState.showAuthor,
                        onOpacityChange = { viewModel.setOverlayOpacity(it) },
                        onToggleWatermark = { viewModel.toggleWatermark() },
                        onToggleAuthor = { viewModel.toggleAuthor() }
                    )
                }
            }
        }
    }

    if (showQuotePicker) {
        QuotePickerSheet(
            quotes = availableQuotes,
            onSelectQuote = {
                viewModel.setQuote(it)
                showQuotePicker = false
            },
            onRollRandom = {
                viewModel.loadRandomQuote()
                showQuotePicker = false
            },
            onDismiss = { showQuotePicker = false }
        )
    }
}

@Composable
fun StudioPreviewCard(
    uiState: StudioUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customBitmap = remember(uiState.background) {
        if (uiState.background is StudioBackground.CustomImage) {
            try {
                context.contentResolver.openInputStream(uiState.background.uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            } catch (_: Exception) {
                null
            }
        } else null
    }

    Card(
        modifier = modifier
            .aspectRatio(uiState.aspectRatio.ratio)
            .shadow(12.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    when (val bg = uiState.background) {
                        is StudioBackground.Solid -> Modifier.background(Color(bg.colorHex))
                        is StudioBackground.Gradient -> Modifier.background(
                            Brush.verticalGradient(
                                colors = listOf(Color(bg.startColorHex), Color(bg.endColorHex))
                            )
                        )
                        is StudioBackground.CustomImage -> Modifier.background(Color.DarkGray)
                    }
                )
        ) {
            // Background Image if custom
            if (customBitmap != null) {
                androidx.compose.foundation.Image(
                    bitmap = customBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Dark Dimmer Overlay
            if (uiState.overlayOpacity > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = uiState.overlayOpacity))
                )
            }

            // Text Content Layer
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = when (uiState.textAlign) {
                    TextAlign.Left, TextAlign.Start -> Alignment.Start
                    TextAlign.Right, TextAlign.End -> Alignment.End
                    else -> Alignment.CenterHorizontally
                }
            ) {
                val textColor = Color(uiState.textColor.colorHex)

                Text(
                    text = "“${uiState.quoteText}”",
                    color = textColor,
                    fontFamily = uiState.font.composeFont,
                    fontSize = (uiState.fontSizeSp * 0.75f).sp, // Scaled for mobile preview
                    lineHeight = (uiState.fontSizeSp * 1.05f).sp,
                    textAlign = uiState.textAlign,
                    style = MaterialTheme.typography.bodyLarge
                )

                if (uiState.showAuthor && uiState.author.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "— ${uiState.author}",
                        color = textColor.copy(alpha = 0.85f),
                        fontFamily = uiState.font.composeFont,
                        fontSize = (uiState.fontSizeSp * 0.45f).sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = uiState.textAlign
                    )
                }
            }

            // Discreet Watermark
            if (uiState.showWatermark) {
                Text(
                    text = "SoulQuote",
                    color = Color(uiState.textColor.colorHex).copy(alpha = 0.5f),
                    fontFamily = StudioFont.SansSerif.composeFont,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                )
            }
        }
    }
}

@Composable
fun FormatControls(
    currentRatio: StudioAspectRatio,
    onSelectRatio: (StudioAspectRatio) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Canvas Aspect Ratio",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StudioAspectRatio.values().forEach { ratio ->
                val selected = currentRatio == ratio
                FilterChip(
                    selected = selected,
                    onClick = { onSelectRatio(ratio) },
                    label = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = ratio.title,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                            Text(
                                text = ratio.name,
                                fontSize = 10.sp,
                                color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun BackgroundControls(
    currentBackground: StudioBackground,
    onSelectBackground: (StudioBackground) -> Unit,
    onPickCustomPhoto: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Solid Mindful Palettes",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedButton(
                onClick = onPickCustomPhoto,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp).padding(end = 4.dp)
                )
                Text("Gallery", fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StudioBackground.solidPresets.forEach { solid ->
                val isSelected = currentBackground is StudioBackground.Solid && currentBackground.colorHex == solid.colorHex
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(solid.colorHex))
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                        .clickable { onSelectBackground(solid) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (solid.colorHex == 0xFFF8F6F0) Color.Black else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Atmospheric Gradients",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StudioBackground.gradientPresets.forEach { grad ->
                val isSelected = currentBackground is StudioBackground.Gradient && currentBackground.name == grad.name
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(grad.startColorHex), Color(grad.endColorHex))
                            )
                        )
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                        .clickable { onSelectBackground(grad) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TypographyControls(
    font: StudioFont,
    fontSizeSp: Float,
    textAlign: TextAlign,
    textColor: StudioTextColor,
    onSelectFont: (StudioFont) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onSelectAlign: (TextAlign) -> Unit,
    onSelectTextColor: (StudioTextColor) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Font Family selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StudioFont.values().forEach { item ->
                FilterChip(
                    selected = font == item,
                    onClick = { onSelectFont(item) },
                    label = { Text(item.label, fontFamily = item.composeFont) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Alignment & Colors Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Align Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = { onSelectAlign(TextAlign.Left) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.FormatAlignLeft,
                        contentDescription = "Left",
                        tint = if (textAlign == TextAlign.Left) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
                IconButton(
                    onClick = { onSelectAlign(TextAlign.Center) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatAlignJustify,
                        contentDescription = "Center",
                        tint = if (textAlign == TextAlign.Center) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
                IconButton(
                    onClick = { onSelectAlign(TextAlign.Right) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.FormatAlignRight,
                        contentDescription = "Right",
                        tint = if (textAlign == TextAlign.Right) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }

            // Color Palette
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StudioTextColor.values().forEach { col ->
                    val isSelected = textColor == col
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(col.colorHex))
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                            .clickable { onSelectTextColor(col) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Font Size Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FormatSize,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = fontSizeSp,
                onValueChange = onFontSizeChange,
                valueRange = 16f..40f,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
            Text(
                text = "${fontSizeSp.toInt()}sp",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun EffectsControls(
    overlayOpacity: Float,
    showWatermark: Boolean,
    showAuthor: Boolean,
    onOpacityChange: (Float) -> Unit,
    onToggleWatermark: () -> Unit,
    onToggleAuthor: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Dimmer Overlay (${(overlayOpacity * 100).toInt()}%)",
                style = MaterialTheme.typography.labelMedium
            )
        }
        Slider(
            value = overlayOpacity,
            onValueChange = onOpacityChange,
            valueRange = 0f..0.85f
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Show Author Name", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = showAuthor, onCheckedChange = { onToggleAuthor() })
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "SoulQuote Watermark", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = showWatermark, onCheckedChange = { onToggleWatermark() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotePickerSheet(
    quotes: List<Quote>,
    onSelectQuote: (Quote) -> Unit,
    onRollRandom: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Inspiration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(
                    onClick = onRollRandom,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp).padding(end = 4.dp)
                    )
                    Text("Random", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.height(380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(quotes) { quote ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectQuote(quote) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "“${quote.text}”",
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "— ${quote.author}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}