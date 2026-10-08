package uz.ttpu.movieshelf.domain.repository

import uz.ttpu.movieshelf.domain.model.MoviesResult

// Owned by the domain layer; the data layer implements it (Dependency Inversion).
interface MovieRepository {
    suspend fun getMovies(): MoviesResult
    suspend fun toggleFavorite(movieId: Int): Boolean
}
