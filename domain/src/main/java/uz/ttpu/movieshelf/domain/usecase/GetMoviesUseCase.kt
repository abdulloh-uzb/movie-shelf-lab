package uz.ttpu.movieshelf.domain.usecase

import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.repository.MovieRepository

// Task 5: this use case first depended on MovieRepositoryImpl, so the domain (inner layer)
// imported a class from the data layer (outer layer). That breaks the Dependency Rule:
// source-code dependencies must point inward only.
// Task 6 fixed it: the use case now depends on the MovieRepository interface owned by
// the domain, and MovieRepositoryImpl in the data layer implements it.
class GetMoviesUseCase(private val repository: MovieRepository) {

    // Business rule: best-rated first, ties sorted alphabetically by title.
    suspend operator fun invoke(): MoviesResult {
        val result = repository.getMovies()
        val sorted = result.movies.sortedWith(
            compareByDescending<Movie> { it.rating }.thenBy { it.title }
        )
        return result.copy(movies = sorted)
    }
}
