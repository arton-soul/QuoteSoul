package com.soulquote.app.presentation.studio

import android.content.Context
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.soulquote.app.core.di.AppContainer
import com.soulquote.app.core.studio.QuoteImageExporter
import com.soulquote.app.domain.model.Quote
import com.soulquote.app.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudioViewModel(
    private val quoteRepository: QuoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudioUiState())
    val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

    val availableQuotes: StateFlow<List<Quote>> = quoteRepository.getAllQuotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuote(quote: Quote) {
        _uiState.value = _uiState.value.copy(
            quoteId = quote.id,
            quoteText = quote.text,
            author = quote.author
        )
    }

    fun loadQuoteById(quoteId: String) {
        viewModelScope.launch {
            val quote = quoteRepository.getQuoteById(quoteId)
            if (quote != null) {
                setQuote(quote)
            }
        }
    }

    fun loadRandomQuote() {
        viewModelScope.launch {
            val quote = quoteRepository.getRandomQuote()
            if (quote != null) {
                setQuote(quote)
            }
        }
    }

    fun setAspectRatio(ratio: StudioAspectRatio) {
        _uiState.value = _uiState.value.copy(aspectRatio = ratio)
    }

    fun setBackground(background: StudioBackground) {
        _uiState.value = _uiState.value.copy(background = background)
    }

    fun setFont(font: StudioFont) {
        _uiState.value = _uiState.value.copy(font = font)
    }

    fun setFontSize(sizeSp: Float) {
        _uiState.value = _uiState.value.copy(fontSizeSp = sizeSp.coerceIn(16f, 40f))
    }

    fun setTextAlign(alignment: TextAlign) {
        _uiState.value = _uiState.value.copy(textAlign = alignment)
    }

    fun setTextColor(textColor: StudioTextColor) {
        _uiState.value = _uiState.value.copy(textColor = textColor)
    }

    fun setOverlayOpacity(opacity: Float) {
        _uiState.value = _uiState.value.copy(overlayOpacity = opacity.coerceIn(0f, 0.85f))
    }

    fun toggleWatermark() {
        _uiState.value = _uiState.value.copy(showWatermark = !_uiState.value.showWatermark)
    }

    fun toggleAuthor() {
        _uiState.value = _uiState.value.copy(showAuthor = !_uiState.value.showAuthor)
    }

    fun saveToGallery(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, exportStatusMessage = null)
            try {
                val uri = QuoteImageExporter.saveToGallery(context, _uiState.value)
                if (uri != null) {
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        exportStatusMessage = "Saved to Gallery (Pictures/SoulQuote)"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        exportStatusMessage = "Failed to save image"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportStatusMessage = "Error: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
        }
    }

    fun shareImage(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, exportStatusMessage = null)
            try {
                QuoteImageExporter.shareQuoteImage(context, _uiState.value)
                _uiState.value = _uiState.value.copy(isExporting = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportStatusMessage = "Share failed: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
        }
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(exportStatusMessage = null)
    }
}

class StudioViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudioViewModel::class.java)) {
            return StudioViewModel(
                quoteRepository = appContainer.quoteRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}