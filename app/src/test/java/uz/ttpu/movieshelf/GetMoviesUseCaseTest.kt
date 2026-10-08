package uz.ttpu.movieshelf

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.repository.MovieRepository
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase

class FakeMovieRepository(
    private val result: MoviesResult,
) : MovieRepository {
    override suspend fun getMovies() = result
    override suspend fun toggleFavorite(movieId: Int) = true
}

class GetMoviesUseCaseTest {

    @Test
    fun movies_are_sorted_by_rating_desc_then_title() = runBlocking<Unit> {
        // Arrange
        val movies = listOf(
            Movie(1, "B", 2020, 8.0),
            Movie(2, "A", 2020, 8.0),
            Movie(3, "C", 2020, 9.1),
        )
        val useCase = GetMoviesUseCase(FakeMovieRepository(MoviesResult(movies, isFromCache = false)))

        // Act
        val result = useCase()

        // Assert
        assertEquals(listOf("C", "A", "B"), result.movies.map { it.title })
    }

    @Test
    fun is_from_cache_flag_is_passed_through() = runBlocking<Unit> {
        val useCase = GetMoviesUseCase(FakeMovieRepository(MoviesResult(emptyList(), isFromCache = true)))

        assertTrue(useCase().isFromCache)
    }

    @Test
    fun favorite_movies_use_case_returns_only_favorites() = runBlocking<Unit> {
        val movies = listOf(
            Movie(1, "A", 2020, 8.0, isFavorite = true),
            Movie(2, "B", 2020, 7.0),
            Movie(3, "C", 2020, 9.0, isFavorite = true),
        )
        val getMovies = GetMoviesUseCase(FakeMovieRepository(MoviesResult(movies, isFromCache = false)))

        val result = GetFavoriteMoviesUseCase(getMovies)()

        assertEquals(listOf("C", "A"), result.movies.map { it.title })
    }
}
