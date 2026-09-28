package com.soulquote.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Explore : Screen("explore", "Explore", Icons.Default.Search)
    object Studio : Screen("studio", "Studio", Icons.Default.Palette)
    object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
    object Meditation : Screen("meditation", "Meditate", Icons.Default.SelfImprovement)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    fun getTitle(strings: com.soulquote.app.core.localization.AppStrings): String = when (this) {
        Home -> strings.navHome
        Explore -> strings.navExplore
        Studio -> strings.navStudio
        Favorites -> strings.navFavorites
        Meditation -> strings.navMeditation
        Settings -> strings.navSettings
    }

    companion object {
        val bottomNavItems = listOf(Home, Explore, Meditation, Studio, Settings)
    }
}