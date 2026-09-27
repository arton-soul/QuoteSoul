package com.soulquote.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.soulquote.app.presentation.favorites.FavoritesScreen
import com.soulquote.app.presentation.home.HomeScreen
import com.soulquote.app.presentation.quotes.ExploreQuotesScreen
import com.soulquote.app.presentation.quotes.QuoteViewModel
import com.soulquote.app.presentation.settings.SettingsScreen

@Composable
fun SoulQuoteNavGraph(
    navController: NavHostController,
    viewModel: QuoteViewModel,
    modifier: Modifier = Modifier
) {
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
                }
            )
        }

        composable(Screen.Explore.route) {
            ExploreQuotesScreen(
                viewModel = viewModel
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                viewModel = viewModel
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
