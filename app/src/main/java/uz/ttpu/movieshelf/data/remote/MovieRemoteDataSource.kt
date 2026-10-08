package uz.ttpu.movieshelf.data.remote

data class MovieDto(
    val id: Int,
    val title: String,
    val releaseYear: Int,
    val score: Double,
)

// All data-source functions are suspend because they do I/O (network, disk),
// and I/O must not block the main thread.
interface MovieRemoteDataSource {
    suspend fun fetchMovies(): List<MovieDto>
}
