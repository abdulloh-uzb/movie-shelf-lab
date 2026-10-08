package uz.ttpu.movieshelf.data.local

import uz.ttpu.movieshelf.data.remote.MovieDto

// suspend: reading/writing storage is I/O and must not block the main thread.
interface MovieLocalDataSource {
    suspend fun getCachedMovies(): List<MovieDto>?   // null = nothing cached yet
    suspend fun saveMovies(movies: List<MovieDto>)
    suspend fun getFavoriteIds(): Set<Int>
    suspend fun setFavorite(id: Int, favorite: Boolean)
}
