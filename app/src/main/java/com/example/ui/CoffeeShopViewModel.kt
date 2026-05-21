package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.FavoriteCoffeeShop
import com.example.data.repository.CoffeeShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val shops: List<FavoriteCoffeeShop>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

class CoffeeShopViewModel(private val repository: CoffeeShopRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("Seattle, WA")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter = _selectedFilter.asStateFlow()

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchUiState = _searchUiState.asStateFlow()

    private val _selectedCoffeeShop = MutableStateFlow<FavoriteCoffeeShop?>(null)
    val selectedCoffeeShop = _selectedCoffeeShop.asStateFlow()

    val favorites: StateFlow<List<FavoriteCoffeeShop>> = repository.allFavorites
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        performSearch("Seattle, WA")
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun selectCoffeeShop(shop: FavoriteCoffeeShop?) {
        _selectedCoffeeShop.value = shop
    }

    fun performSearch(query: String = _searchQuery.value) {
        if (query.isBlank()) return
        _searchQuery.value = query
        viewModelScope.launch {
            _searchUiState.value = SearchUiState.Loading
            try {
                val results = repository.searchLocalCoffeeShops(query)
                _searchUiState.value = SearchUiState.Success(results)
            } catch (e: Exception) {
                _searchUiState.value = SearchUiState.Error(
                    e.localizedMessage ?: "An error occurred while fetching coffee shops from Gemini."
                )
            }
        }
    }

    fun toggleFavorite(shop: FavoriteCoffeeShop) {
        viewModelScope.launch {
            repository.toggleFavorite(shop)
        }
    }

    class Factory(private val repository: CoffeeShopRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CoffeeShopViewModel::class.java)) {
                return CoffeeShopViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
