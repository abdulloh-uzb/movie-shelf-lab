/*
 * Task 15 — tracing one heart tap through the layers.
 *
 * (1) Order of calls (flow of control, outside -> in):
 *     MovieItem (UI) -> MovieListViewModel.onFavoriteClick -> ToggleFavoriteUseCase.invoke
 *     -> MovieRepositoryImpl.toggleFavorite -> SharedPrefsMovieLocalDataSource.setFavorite
 *
 * (2) Source-code dependencies (who imports whom):
 *     MovieItem -> MovieListViewModel -> ToggleFavoriteUseCase -> MovieRepository (interface, domain)
 *     MovieRepositoryImpl -> MovieRepository (implements it)
 *     MovieRepositoryImpl -> MovieLocalDataSource <- SharedPrefsMovieLocalDataSource
 *     The arrow MovieRepositoryImpl -> MovieRepository points opposite to the call
 *     (the use case calls the repository, but the repository depends on the domain).
 *     This is Dependency Inversion: the domain owns the interface, so all
 *     dependencies point inward even though control flows outward.
 *
 * (3) The use case lives in the pure Kotlin domain module, which has no Android on its
 *     classpath, so android.util.Log does not exist there. The clean way: declare an
 *     interface Logger in the domain and implement it in an outer layer (e.g. with Log.d),
 *     then inject it from AppContainer — Dependency Inversion again.
 */
package uz.ttpu.movieshelf.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.ToggleFavoriteUseCase
import java.io.IOException

sealed interface MovieListUiState {
    data object Loading : MovieListUiState
    data class Success(
        val movies: List<Movie>,
        val isFromCache: Boolean,
        val favoritesOnly: Boolean = false,
    ) : MovieListUiState
    data class Error(val message: String) : MovieListUiState
}

class MovieListViewModel(
    private val getMovies: GetMoviesUseCase,
    private val getFavoriteMovies: GetFavoriteMoviesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val state: StateFlow<MovieListUiState> = _state.asStateFlow()

    private var favoritesOnly = false

    init { load() }

    fun onRefresh() = load()

    fun onFavoritesOnlyChange(enabled: Boolean) {
        favoritesOnly = enabled
        load()
    }

    fun onFavoriteClick(movieId: Int) {
        viewModelScope.launch {
            val isFavorite = toggleFavorite(movieId)
            _state.update { current ->
                if (current !is MovieListUiState.Success) return@update current
                val movies = current.movies
                    .map { if (it.id == movieId) it.copy(isFavorite = isFavorite) else it }
                    .filter { !current.favoritesOnly || it.isFavorite }
                current.copy(movies = movies)
            }
        }
    }

    private fun load() {
        _state.value = MovieListUiState.Loading
        viewModelScope.launch {
            _state.value = try {
                val result = if (favoritesOnly) getFavoriteMovies() else getMovies()
                MovieListUiState.Success(result.movies, result.isFromCache, favoritesOnly)
            } catch (e: IOException) {
                MovieListUiState.Error("Can't load movies. Check your connection and try again.")
            }
        }
    }
}
