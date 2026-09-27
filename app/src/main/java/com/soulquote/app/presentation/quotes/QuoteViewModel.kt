package com.soulquote.app.presentation.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soulquote.app.domain.model.Quote
import com.soulquote.app.domain.model.QuoteCategory
import com.soulquote.app.domain.usecase.GetCategoriesUseCase
import com.soulquote.app.domain.usecase.GetDailyQuoteUseCase
import com.soulquote.app.domain.usecase.GetFavoriteQuotesUseCase
import com.soulquote.app.domain.usecase.GetQuotesUseCase
import com.soulquote.app.domain.usecase.GetRandomQuoteUseCase
import com.soulquote.app.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val dailyQuote: Quote? = null,
    val categories: List<QuoteCategory> = emptyList(),
    val isLoading: Boolean = true
)

data class ExploreUiState(
    val quotes: List<Quote> = emptyList(),
    val categories: List<QuoteCategory> = emptyList(),
    val selectedCategoryId: String = "all",
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

data class FavoritesUiState(
    val favoriteQuotes: List<Quote> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class QuoteViewModel(
    private val getDailyQuoteUseCase: GetDailyQuoteUseCase,
    private val getQuotesUseCase: GetQuotesUseCase,
    private val getFavoriteQuotesUseCase: GetFavoriteQuotesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getRandomQuoteUseCase: GetRandomQuoteUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow("all")
    val selectedCategoryId = _selectedCategoryId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedQuoteForDetail = MutableStateFlow<Quote?>(null)
    val selectedQuoteForDetail = _selectedQuoteForDetail.asStateFlow()

    private val _dailyQuote = MutableStateFlow<Quote?>(null)
    val dailyQuote = _dailyQuote.asStateFlow()

    val categories: StateFlow<List<QuoteCategory>> = getCategoriesUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val exploreQuotes: StateFlow<List<Quote>> = combine(
        _selectedCategoryId.flatMapLatest { catId -> getQuotesUseCase(catId) },
        _searchQuery
    ) { quotes, query ->
        if (query.isBlank()) {
            quotes
        } else {
            quotes.filter {
                it.text.contains(query, ignoreCase = true) ||
                it.author.contains(query, ignoreCase = true) ||
                it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteQuotes: StateFlow<List<Quote>> = getFavoriteQuotesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadDailyQuote()
    }

    fun loadDailyQuote() {
        viewModelScope.launch {
            _dailyQuote.value = getDailyQuoteUseCase()
        }
    }

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun showQuoteDetail(quote: Quote?) {
        _selectedQuoteForDetail.value = quote
    }

    fun toggleFavorite(quote: Quote) {
        viewModelScope.launch {
            toggleFavoriteUseCase(quote.id, !quote.isFavorite)
            // If the toggled quote is the daily quote, reflect it immediately
            if (_dailyQuote.value?.id == quote.id) {
                _dailyQuote.value = _dailyQuote.value?.copy(isFavorite = !quote.isFavorite)
            }
            if (_selectedQuoteForDetail.value?.id == quote.id) {
                _selectedQuoteForDetail.value = _selectedQuoteForDetail.value?.copy(isFavorite = !quote.isFavorite)
            }
        }
    }
}
