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
import com.soulquote.app.presentation.settings.SettingsViewModel
import com.soulquote.app.presentation.settings.SettingsViewModelFactory
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
            _navTarget.value = target
        }
    }
}

@Composable
fun MainContent(
    quoteViewModel: QuoteViewModel,
    settingsViewModel: SettingsViewModel,
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
            modifier = Modifier.padding(innerPadding)
        )
    }
}