package com.soulquote.app.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.soulquote.app.SoulQuoteApp
import com.soulquote.app.core.notification.NotificationHelper
import com.soulquote.app.core.theme.SoulQuoteTheme
import com.soulquote.app.presentation.navigation.Screen
import com.soulquote.app.presentation.navigation.SoulQuoteNavGraph
import com.soulquote.app.presentation.quotes.QuoteViewModel
import com.soulquote.app.presentation.quotes.QuoteViewModelFactory
import com.soulquote.app.presentation.meditation.MeditationViewModel
import com.soulquote.app.presentation.meditation.MeditationViewModelFactory
import com.soulquote.app.presentation.ambient.AmbientViewModel
import com.soulquote.app.presentation.ambient.AmbientViewModelFactory
import com.soulquote.app.presentation.settings.SettingsViewModel
import com.soulquote.app.presentation.settings.SettingsViewModelFactory
import com.soulquote.app.presentation.studio.StudioViewModel
import com.soulquote.app.presentation.studio.StudioViewModelFactory
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : ComponentActivity() {

    private val quoteViewModel: QuoteViewModel by viewModels {
        val appContainer = (application as SoulQuoteApp).appContainer
        QuoteViewModelFactory(appContainer)
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        val appContainer = (application as SoulQuoteApp).appContainer
        SettingsViewModelFactory(appContainer)
    }

    private val studioViewModel: StudioViewModel by viewModels {
        val appContainer = (application as SoulQuoteApp).appContainer
        StudioViewModelFactory(appContainer)
    }

    private val meditationViewModel: MeditationViewModel by viewModels {
        val appContainer = (application as SoulQuoteApp).appContainer
        MeditationViewModelFactory(appContainer)
    }

    private val ambientViewModel: AmbientViewModel by viewModels {
        val appContainer = (application as SoulQuoteApp).appContainer
        AmbientViewModelFactory(appContainer)
    }

    private val _navTarget = MutableStateFlow<String?>(null)
    val navTarget: StateFlow<String?> = _navTarget.asStateFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleNavIntent(intent)

        setContent {
            SoulQuoteTheme {
                MainContent(
                    quoteViewModel = quoteViewModel,
                    settingsViewModel = settingsViewModel,
                    studioViewModel = studioViewModel,
                    meditationViewModel = meditationViewModel,
                    ambientViewModel = ambientViewModel,
                    navTargetState = navTarget,
                    onConsumeNavTarget = { _navTarget.value = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNavIntent(intent)
    }

    private fun handleNavIntent(intent: Intent?) {
        val target = intent?.getStringExtra(NotificationHelper.EXTRA_NAV_TARGET)
        if (target != null) {
            if (target == "ambient") {
                ambientViewModel.selectTab(1)
            } else if (target == "meditation") {
                ambientViewModel.selectTab(0)
            }
            _navTarget.value = target
        }
        val playId = intent?.getStringExtra("extra_play_meditation_id")
        if (playId != null) {
            lifecycleScope.launch {
                meditationViewModel.catalogUiState.collect { state ->
                    val med = state.meditations.find { it.id == playId }
                    if (med != null) {
                        meditationViewModel.playMeditation(med)
                        cancel()
                    }
                }
            }
        }
        val presetId = intent?.getStringExtra("extra_play_ambient_preset")
        if (presetId != null) {
            val preset = com.soulquote.app.domain.model.AmbientPreset.DEFAULT_PRESETS.find { it.id == presetId }
            if (preset != null) {
                ambientViewModel.applyPreset(preset)
            }
        }
        val contentAction = intent?.getStringExtra("extra_content_action")
        if (contentAction == "check") {
            settingsViewModel.checkForUpdates()
        } else if (contentAction == "apply") {
            settingsViewModel.applyContentUpdate()
        }
    }
}

@Composable
fun MainContent(
    quoteViewModel: QuoteViewModel,
    settingsViewModel: SettingsViewModel,
    studioViewModel: StudioViewModel,
    meditationViewModel: MeditationViewModel,
    ambientViewModel: AmbientViewModel,
    navTargetState: StateFlow<String?>,
    onConsumeNavTarget: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val targetRoute by navTargetState.collectAsState()

    LaunchedEffect(targetRoute) {
        targetRoute?.let { target ->
            when (target) {
                "home" -> {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
                "settings" -> {
                    if (currentRoute != Screen.Settings.route) {
                        navController.navigate(Screen.Settings.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
                "explore" -> {
                    if (currentRoute != Screen.Explore.route) {
                        navController.navigate(Screen.Explore.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
                "meditation", "ambient" -> {
                    if (currentRoute != Screen.Meditation.route) {
                        navController.navigate(Screen.Meditation.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
                "studio" -> {
                    if (currentRoute != Screen.Studio.route) {
                        navController.navigate(Screen.Studio.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
                "favorites" -> {
                    if (currentRoute != Screen.Favorites.route) {
                        navController.navigate(Screen.Favorites.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
            onConsumeNavTarget()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Screen.bottomNavItems.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                if (screen.route == Screen.Home.route) {
                                    navController.popBackStack(Screen.Home.route, inclusive = false)
                                } else {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        SoulQuoteNavGraph(
            navController = navController,
            viewModel = quoteViewModel,
            settingsViewModel = settingsViewModel,
            studioViewModel = studioViewModel,
            meditationViewModel = meditationViewModel,
            ambientViewModel = ambientViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}