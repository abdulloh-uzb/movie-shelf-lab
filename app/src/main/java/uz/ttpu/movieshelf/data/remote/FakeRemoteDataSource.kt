package uz.ttpu.movieshelf.data.remote

import kotlinx.coroutines.delay
import java.io.IOException

class FakeRemoteDataSource : MovieRemoteDataSource {
    @Volatile
    var isOnline: Boolean = true

    override suspend fun fetchMovies(): List<MovieDto> {
        delay(800)
        if (!isOnline) throw IOException("no Connection")
        return listOf(
            MovieDto(1, "The Shawshank Redemption", 1994, 9.2),
            MovieDto(2, "The Godfather", 1972, 9.2),
            MovieDto(3, "The Godfather: Part II", 1974, 9.0),
            MovieDto(4, "Odyssey", 2026, 9.2),
            MovieDto(5, "Forrest Gump", 2004, 8.9),
            MovieDto(6, "Gallary", 1994, 9.2),
            MovieDto(7, "Desmoond Doss", 1972, 9.2),
            MovieDto(8, "F1", 2024, 9.0)
        )
    }
}
