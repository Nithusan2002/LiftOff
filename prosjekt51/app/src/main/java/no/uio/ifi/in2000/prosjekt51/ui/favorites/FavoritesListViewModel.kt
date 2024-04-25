package no.uio.ifi.in2000.prosjekt51.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class FavoriteViewModel(private val repository: FavoriteRepository) : ViewModel() {
    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favorites: StateFlow<List<Favorite>> = _favorites

    init {
        loadFavorites()
    }

    fun addFavorite(favorite: Favorite) {
        viewModelScope.launch {
            repository.addFavorite(favorite)
            loadFavorites() // Reload the favorites after adding
        }
    }

    fun deleteFavorite(favorite: Favorite) {
        viewModelScope.launch {
            repository.deleteFavorite(favorite)
            loadFavorites() // Reload the favorites after adding
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _favorites.value = repository.getFavorites()
        }
    }
}
