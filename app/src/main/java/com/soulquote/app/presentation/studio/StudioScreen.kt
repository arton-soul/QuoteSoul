package com.soulquote.app.presentation.studio

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.VerticalAlignCenter
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.soulquote.app.data.local.entity.user.UserQuoteEntity
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
    val userQuotes by viewModel.userQuotes.collectAsStateWithLifecycle()

    var showQuotePicker by remember { mutableStateOf(false) }
    var pickerInitialTab by remember { mutableIntStateOf(0) }
    var showCreateCustomQuoteDialog by remember { mutableStateOf(false) }
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
                    IconButton(onClick = { showCreateCustomQuoteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Tulis Kutipan Baru",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        pickerInitialTab = 0
                        showQuotePicker = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.CollectionsBookmark,
                            contentDescription = "Pilih & Koleksi Kutipan",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { viewModel.saveToGallery(context) },
                        enabled = !uiState.isExporting
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Simpan ke Galeri",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { viewModel.shareImage(context) },
                        enabled = !uiState.isExporting
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Live Preview Canvas Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                StudioPreviewCard(
                    uiState = uiState,
                    onVerticalBiasChange = { viewModel.setVerticalBias(it) }
                )

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

            // Quick Access Row: Kutipan Saya & Tulis Baru
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = {
                        pickerInitialTab = 1
                        showQuotePicker = true
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Kutipan Saya (${userQuotes.size})",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Surface(
                    onClick = { showCreateCustomQuoteDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "+ Tulis Baru",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // 2. Control Tabs
            val tabs = listOf("Format", "Tema", "Teks", "Efek")
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
                    .height(230.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                when (activeTab) {
                    0 -> FormatControls(
                        currentRatio = uiState.aspectRatio,
                        verticalBias = uiState.verticalBias,
                        onSelectRatio = { viewModel.setAspectRatio(it) },
                        onVerticalBiasChange = { viewModel.setVerticalBias(it) }
                    )
                    1 -> BackgroundControls(
                        currentBackground = uiState.background,
                        overlayOpacity = uiState.overlayOpacity,
                        onSelectBackground = { viewModel.setBackground(it) },
                        onOpacityChange = { viewModel.setOverlayOpacity(it) },
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
                        textCardBg = uiState.textCardBg,
                        textCardOpacity = uiState.textCardOpacity,
                        overlayOpacity = uiState.overlayOpacity,
                        showWatermark = uiState.showWatermark,
                        showAuthor = uiState.showAuthor,
                        onSelectTextCardBg = { viewModel.setTextCardBg(it) },
                        onTextCardOpacityChange = { viewModel.setTextCardOpacity(it) },
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
            userQuotes = userQuotes,
            initialTab = pickerInitialTab,
            onSelectQuote = {
                viewModel.setQuote(it)
                showQuotePicker = false
            },
            onSelectUserQuote = {
                viewModel.setCustomQuote(it)
                showQuotePicker = false
            },
            onRollRandom = {
                viewModel.loadRandomQuote()
                showQuotePicker = false
            },
            onCreateCustomQuote = {
                showCreateCustomQuoteDialog = true
            },
            onDeleteUserQuote = {
                viewModel.deleteCustomQuote(it)
            },
            onDismiss = { showQuotePicker = false }
        )
    }

    if (showCreateCustomQuoteDialog) {
        CreateCustomQuoteDialog(
            onSave = { text, author ->
                viewModel.saveCustomQuote(text, author)
                showCreateCustomQuoteDialog = false
                showQuotePicker = false
            },
            onDismiss = { showCreateCustomQuoteDialog = false }
        )
    }
}

@Composable
fun StudioPreviewCard(
    uiState: StudioUiState,
    onVerticalBiasChange: (Float) -> Unit,
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

            // Text Content Layer with Vertical Bias & Drag Gesture
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                contentAlignment = BiasAlignment(0f, uiState.verticalBias)
            ) {
                val hasCardBg = uiState.textCardBg != StudioTextCardBg.None
                val textColor = Color(uiState.textColor.colorHex)

                Box(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { change, dragAmount ->
                                change.consume()
                                val delta = dragAmount / 220f
                                onVerticalBiasChange((uiState.verticalBias + delta).coerceIn(-0.75f, 0.75f))
                            }
                        }
                        .then(
                            if (hasCardBg) {
                                Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Color(uiState.textCardBg.colorHex).copy(alpha = uiState.textCardOpacity)
                                    )
                                    .padding(horizontal = 18.dp, vertical = 14.dp)
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Column(
                        horizontalAlignment = when (uiState.textAlign) {
                            TextAlign.Left, TextAlign.Start -> Alignment.Start
                            TextAlign.Right, TextAlign.End -> Alignment.End
                            else -> Alignment.CenterHorizontally
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                            Spacer(modifier = Modifier.height(10.dp))
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
    verticalBias: Float,
    onSelectRatio: (StudioAspectRatio) -> Unit,
    onVerticalBiasChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Rasio Kanvas",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
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

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Posisi Kutipan (Geser Layar / Tombol Cepat)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { onVerticalBiasChange(-0.55f) },
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.VerticalAlignTop, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Atas", fontSize = 12.sp)
            }
            OutlinedButton(
                onClick = { onVerticalBiasChange(0f) },
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.VerticalAlignCenter, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tengah", fontSize = 12.sp)
            }
            OutlinedButton(
                onClick = { onVerticalBiasChange(0.55f) },
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.VerticalAlignBottom, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bawah", fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Slider(
            value = verticalBias,
            onValueChange = onVerticalBiasChange,
            valueRange = -0.75f..0.75f
        )
    }
}

@Composable
fun BackgroundControls(
    currentBackground: StudioBackground,
    overlayOpacity: Float,
    onSelectBackground: (StudioBackground) -> Unit,
    onOpacityChange: (Float) -> Unit,
    onPickCustomPhoto: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Warna Solid & Galeri",
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
                Text("Foto Galeri", fontSize = 12.sp)
            }
        }

        // If Custom Photo is selected, show direct Dimmer / Opacity slider
        if (currentBackground is StudioBackground.CustomImage) {
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Opasitas / Redup Foto Galeri",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${(overlayOpacity * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Slider(
                        value = overlayOpacity,
                        onValueChange = onOpacityChange,
                        valueRange = 0f..0.85f
                    )
                }
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
            text = "Gradasi Atmosfer",
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
    textCardBg: StudioTextCardBg,
    textCardOpacity: Float,
    overlayOpacity: Float,
    showWatermark: Boolean,
    showAuthor: Boolean,
    onSelectTextCardBg: (StudioTextCardBg) -> Unit,
    onTextCardOpacityChange: (Float) -> Unit,
    onOpacityChange: (Float) -> Unit,
    onToggleWatermark: () -> Unit,
    onToggleAuthor: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Quote Text Card Background Section
        Text(
            text = "Latar Khusus Kalimat Quote",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StudioTextCardBg.values().forEach { bg ->
                val isSelected = textCardBg == bg
                if (bg == StudioTextCardBg.None) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Transparent)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                            .clickable { onSelectTextCardBg(bg) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "None",
                            modifier = Modifier.size(18.dp),
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(bg.colorHex))
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                            .clickable { onSelectTextCardBg(bg) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = if (bg == StudioTextCardBg.Light || bg == StudioTextCardBg.Cream) Color.Black else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        if (textCardBg != StudioTextCardBg.None) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Opasitas Latar Quote (${(textCardOpacity * 100).toInt()}%)",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Slider(
                value = textCardOpacity,
                onValueChange = onTextCardOpacityChange,
                valueRange = 0.15f..1f
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dimmer Overlay Slider
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
                text = "Redup Gambar Kanvas / Dimmer (${(overlayOpacity * 100).toInt()}%)",
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
            Text(text = "Tampilkan Nama Penulis", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = showAuthor, onCheckedChange = { onToggleAuthor() })
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Watermark SoulQuote", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = showWatermark, onCheckedChange = { onToggleWatermark() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotePickerSheet(
    quotes: List<Quote>,
    userQuotes: List<UserQuoteEntity>,
    initialTab: Int = 0,
    onSelectQuote: (Quote) -> Unit,
    onSelectUserQuote: (UserQuoteEntity) -> Unit,
    onRollRandom: () -> Unit,
    onCreateCustomQuote: () -> Unit,
    onDeleteUserQuote: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var pickerTab by remember(initialTab) { mutableIntStateOf(initialTab) }

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
                    text = "Pilih Inspirasi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onCreateCustomQuote,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp).padding(end = 4.dp)
                        )
                        Text("Tulis Kutipan", fontSize = 12.sp)
                    }

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
                        Text("Acak", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TabRow(
                selectedTabIndex = pickerTab,
                containerColor = Color.Transparent
            ) {
                Tab(
                    selected = pickerTab == 0,
                    onClick = { pickerTab = 0 },
                    text = { Text("Katalog (${quotes.size})") }
                )
                Tab(
                    selected = pickerTab == 1,
                    onClick = { pickerTab = 1 },
                    text = { Text("Kutipan Saya (${userQuotes.size})") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (pickerTab == 0) {
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
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
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
            } else {
                if (userQuotes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum Ada Kutipan Pribadi",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tulis kutipan atau kata-kata bijakmu sendiri. Kutipan ini akan disimpan di database pribadimu.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onCreateCustomQuote) {
                                Text("＋ Tulis Kutipan Sekarang")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.height(380.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(userQuotes) { uq ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectUserQuote(uq) },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "“${uq.text}”",
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "— ${uq.author}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteUserQuote(uq.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Hapus Kutipan",
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                            modifier = Modifier.size(18.dp)
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
}

@Composable
fun CreateCustomQuoteDialog(
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Tulis Kutipan Baru", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Kata-kata ini akan disimpan di database pribadimu dan dapat diedit di Quote Studio kapan saja.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Kalimat Kutipan / Renungan") },
                    placeholder = { Text("Tuliskan kata-kata mutiara jiwamu...") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Penulis / Sumber (Opsional)") },
                    placeholder = { Text("Pribadi / Nama kamu") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(text, author) },
                enabled = text.trim().isNotBlank()
            ) {
                Text("Simpan & Pakai")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}