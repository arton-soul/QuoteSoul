package com.soulquote.app.presentation.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.soulquote.app.core.di.AppContainer
import com.soulquote.app.domain.usecase.GetCategoriesUseCase
import com.soulquote.app.domain.usecase.GetDailyQuoteUseCase
import com.soulquote.app.domain.usecase.GetFavoriteQuotesUseCase
import com.soulquote.app.domain.usecase.GetQuotesUseCase
import com.soulquote.app.domain.usecase.GetRandomQuoteUseCase
import com.soulquote.app.domain.usecase.ToggleFavoriteUseCase

class QuoteViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuoteViewModel::class.java)) {
            val quoteRepo = appContainer.quoteRepository
            return QuoteViewModel(
                getDailyQuoteUseCase = GetDailyQuoteUseCase(quoteRepo),
                getQuotesUseCase = GetQuotesUseCase(quoteRepo),
                getFavoriteQuotesUseCase = GetFavoriteQuotesUseCase(quoteRepo),
                toggleFavoriteUseCase = ToggleFavoriteUseCase(quoteRepo),
                getRandomQuoteUseCase = GetRandomQuoteUseCase(quoteRepo),
                getCategoriesUseCase = GetCategoriesUseCase(quoteRepo),
                userQuoteDao = appContainer.userDatabase.userQuoteDao()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
