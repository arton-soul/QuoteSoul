package com.soulquote.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.soulquote.app.domain.model.Quote
import com.soulquote.app.presentation.favorites.FavoritesScreen
import com.soulquote.app.presentation.home.HomeScreen
import com.soulquote.app.presentation.quotes.ExploreQuotesScreen
import com.soulquote.app.presentation.quotes.QuoteViewModel
import com.soulquote.app.presentation.meditation.MeditationCatalogScreen
import com.soulquote.app.presentation.meditation.MeditationViewModel
import com.soulquote.app.presentation.ambient.AmbientViewModel
import com.soulquote.app.presentation.journal.JournalViewModel
import com.soulquote.app.presentation.settings.SettingsScreen
import com.soulquote.app.presentation.settings.SettingsViewModel
import com.soulquote.app.presentation.studio.StudioScreen
import com.soulquote.app.presentation.studio.StudioViewModel

@Composable
fun SoulQuoteNavGraph(
    navController: NavHostController,
    viewModel: QuoteViewModel,
    settingsViewModel: SettingsViewModel,
    studioViewModel: StudioViewModel,
    meditationViewModel: MeditationViewModel,
    ambientViewModel: AmbientViewModel,
    journalViewModel: JournalViewModel,
    modifier: Modifier = Modifier
) {
    val navigateToStudioWithQuote: (Quote) -> Unit = { quote ->
        studioViewModel.setQuote(quote)
        navController.navigate(Screen.Studio.route) {
            popUpTo(Screen.Home.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToCategory = { categoryId ->
                    viewModel.selectCategory(categoryId)
                    navController.navigate(Screen.Explore.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToStudio = navigateToStudioWithQuote,
                onNavigateToMeditation = {
                    ambientViewModel.selectTab(0)
                    navController.navigate(Screen.Meditation.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToAmbient = {
                    ambientViewModel.selectTab(1)
                    navController.navigate(Screen.Meditation.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                journalViewModel = journalViewModel
            )
        }

        composable(Screen.Explore.route) {
            ExploreQuotesScreen(
                viewModel = viewModel,
                onNavigateToStudio = navigateToStudioWithQuote
            )
        }

        composable(Screen.Meditation.route) {
            MeditationCatalogScreen(
                viewModel = meditationViewModel,
                ambientViewModel = ambientViewModel
            )
        }

        composable(Screen.Studio.route) {
            StudioScreen(
                viewModel = studioViewModel
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                viewModel = viewModel,
                onNavigateToStudio = navigateToStudioWithQuote
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel
            )
        }
    }
}