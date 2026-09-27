package com.soulquote.app.presentation.settings

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import android.content.Intent
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.core.content.FileProvider
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.Locale

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val contentUpdateState by viewModel.contentUpdateUiState.collectAsStateWithLifecycle()
    val backupState by viewModel.backupUiState.collectAsStateWithLifecycle()

    val restoreFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader().use { it.readText() }
                }
                if (!jsonString.isNullOrBlank()) {
                    viewModel.restoreBackup(jsonString)
                }
            } catch (e: Exception) {
                // Ignore or log error
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.setDailyQuoteEnabled(true)
        }
    }

    val scrollState = rememberScrollState()
    val activity = context as? Activity
    val scrollToBackup = activity?.intent?.getBooleanExtra("extra_scroll_to_backup", false) == true

    LaunchedEffect(scrollToBackup) {
        if (scrollToBackup) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Notifications & Reminders",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Daily Quote
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Quote",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        val formattedTime = String.format(
                            Locale.getDefault(),
                            "%02d:%02d %s",
                            if (settings.dailyQuoteHour % 12 == 0) 12 else settings.dailyQuoteHour % 12,
                            settings.dailyQuoteMinute,
                            if (settings.dailyQuoteHour < 12) "AM" else "PM"
                        )
                        Text(
                            text = "Scheduled at $formattedTime",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Switch(
                        checked = settings.dailyQuoteEnabled,
                        onCheckedChange = { isChecked ->
                            if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !viewModel.hasNotificationPermission()) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.setDailyQuoteEnabled(isChecked)
                            }
                        }
                    )
                }

                if (settings.dailyQuoteEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    viewModel.setDailyQuoteTime(hourOfDay, minute)
                                },
                                settings.dailyQuoteHour,
                                settings.dailyQuoteMinute,
                                false
                            ).show()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AccessTime, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = String.format(
                                Locale.getDefault(),
                                "Change Time (%02d:%02d)",
                                settings.dailyQuoteHour,
                                settings.dailyQuoteMinute
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // 2. Meditation Reminder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Meditation Reminder",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        val formattedMeditationTime = String.format(
                            Locale.getDefault(),
                            "%02d:%02d %s",
                            if (settings.meditationReminderHour % 12 == 0) 12 else settings.meditationReminderHour % 12,
                            settings.meditationReminderMinute,
                            if (settings.meditationReminderHour < 12) "AM" else "PM"
                        )
                        Text(
                            text = "Scheduled at $formattedMeditationTime",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Switch(
                        checked = settings.meditationReminderEnabled,
                        onCheckedChange = { isChecked ->
                            if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !viewModel.hasNotificationPermission()) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.setMeditationReminderEnabled(isChecked)
                            }
                        }
                    )
                }

                if (settings.meditationReminderEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    viewModel.setMeditationReminderTime(hourOfDay, minute)
                                },
                                settings.meditationReminderHour,
                                settings.meditationReminderMinute,
                                false
                            ).show()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AccessTime, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = String.format(
                                Locale.getDefault(),
                                "Change Time (%02d:%02d)",
                                settings.meditationReminderHour,
                                settings.meditationReminderMinute
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // 3. Vibration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Gentle Vibration",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "Haptic pulse with sound notification",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Switch(
                        checked = settings.vibrationEnabled,
                        onCheckedChange = { viewModel.setVibrationEnabled(it) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // 4. Test Notification Buttons
                Text(
                    text = "Sound & Notification Test",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = { viewModel.triggerTestDailyQuoteNotification() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Bell", style = MaterialTheme.typography.labelMedium)
                    }

                    FilledTonalButton(
                        onClick = { viewModel.triggerTestMeditationNotification() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Spa, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Bowl", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content & Offline Storage Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pembaruan Konten",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "v${contentUpdateState.currentVersion}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Statistik Konten", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "${contentUpdateState.totalQuotes} Kutipan • ${contentUpdateState.totalMeditations} Meditasi",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Storage Strategy", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "Dual Isolated Databases",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Kanal Distribusi", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = if (contentUpdateState.remoteManifestUrl.isNullOrBlank())
                            "Google Drive (Auto/Local)"
                        else
                            "Google Drive Cloud",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Status Message Banner (if available)
                contentUpdateState.statusMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (contentUpdateState.isUpToDate)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (contentUpdateState.isUpToDate) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (contentUpdateState.isUpToDate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.dismissStatusMessage() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Tutup",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Update Available Card
                contentUpdateState.updateAvailable?.let { manifest ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Paket Pembaruan v${manifest.contentVersion}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${(manifest.packageSizeBytes / 1024).coerceAtLeast(1)} KB",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = manifest.changelog,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (manifest.quotesCount > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "+${manifest.quotesCount} kutipan baru ditambahkan",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (contentUpdateState.isUpdating) {
                                Column {
                                    LinearProgressIndicator(
                                        progress = { contentUpdateState.updateProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = contentUpdateState.updateStepDescription ?: "Menerapkan pembaruan...",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.applyContentUpdate() },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDownload,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Perbarui Sekarang (v${manifest.contentVersion})")
                                }
                            }
                        }
                    }
                }

                // Check for updates button (if not already showing update package)
                if (contentUpdateState.updateAvailable == null && !contentUpdateState.isUpdating) {
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = { viewModel.checkForUpdates() },
                        enabled = !contentUpdateState.isChecking,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (contentUpdateState.isChecking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Memeriksa Server...")
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Periksa Pembaruan Konten")
                        }
                    }
                }
            }
        }

        // User Data Backup & Privacy Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cadangan & Privasi Data",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "100% Privat",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Data pribadi tersimpan secara lokal dan terisolasi mandiri dari pembaruan konten aplikasi.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Statistik Pribadi", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "${backupState.favoritesCount} Favorit • ${backupState.historyCount} Riwayat",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Backup Status Banner
                backupState.backupMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.dismissBackupMessage() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Tutup",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            viewModel.exportBackup { file ->
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "com.dearyoti.soulquote.fileprovider",
                                    file
                                )
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/json"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    putExtra(Intent.EXTRA_SUBJECT, "SoulQuote Data Backup")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Simpan / Bagikan Cadangan Data"))
                            }
                        },
                        enabled = !backupState.isExporting,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (backupState.isExporting) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ekspor JSON", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    OutlinedButton(
                        onClick = { restoreFileLauncher.launch("application/json") },
                        enabled = !backupState.isRestoring,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (backupState.isRestoring) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pulihkan", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // About SoulQuote
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "About SoulQuote",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "SoulQuote v0.3.1 • Offline-first Inspiration & Meditation",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Crafted with serenity, privacy, and local data persistence.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}