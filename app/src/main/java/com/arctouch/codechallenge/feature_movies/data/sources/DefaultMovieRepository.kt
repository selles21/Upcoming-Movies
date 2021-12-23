package com.arctouch.codechallenge.feature_movies.data.sources

import com.arctouch.codechallenge.feature_movies.data.sources.remote.MoviesRemoteDataSource
import com.arctouch.codechallenge.feature_movies.domain.model.GenreResponse
import com.arctouch.codechallenge.feature_movies.domain.model.Movie
import com.arctouch.codechallenge.feature_movies.domain.model.UpcomingMoviesResponse
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class DefaultMovieRepository : KoinComponent {
    private val moviesRemoteDataSource: MoviesRemoteDataSource by inject()

    companion object {
        @Volatile
        private var INSTANCE: DefaultMovieRepository? = null

        fun getRepository(): DefaultMovieRepository {
            return INSTANCE ?: synchronized(this) {
                DefaultMovieRepository().also {
                    INSTANCE = it
                }
            }
        }
    }

    suspend fun getGenres(apiKey: String, language: String): GenreResponse {
        return moviesRemoteDataSource.getGenres(apiKey, language)
    }

    suspend fun getUpcomingMovies(
        apiKey: String,
        language: String,
        page: Long,
        region: String
    ): UpcomingMoviesResponse {
        return moviesRemoteDataSource.getUpcomingMovies(apiKey, language, page, region)
    }

    suspend fun getSearchUpcomingMovies(
        apiKey: String,
        language: String,
        query: String,
        region: String
    ): UpcomingMoviesResponse {
        return moviesRemoteDataSource.getsearchUpcomingMovies(apiKey, language, query, region)
    }

    suspend fun getMovie(id: Long, apiKey: String, language: String): Movie {
        return moviesRemoteDataSource.getMovie(id, apiKey, language)
    }
}