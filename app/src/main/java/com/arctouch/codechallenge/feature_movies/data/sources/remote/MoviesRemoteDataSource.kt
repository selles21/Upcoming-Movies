package com.arctouch.codechallenge.feature_movies.data.sources.remote

import com.arctouch.codechallenge.feature_movies.data.sources.remote.api.TmdbApi
import com.arctouch.codechallenge.feature_movies.data.sources.MoviesDataSource
import com.arctouch.codechallenge.feature_movies.domain.model.GenreResponse
import com.arctouch.codechallenge.feature_movies.domain.model.Movie
import com.arctouch.codechallenge.feature_movies.domain.model.UpcomingMoviesResponse
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class MoviesRemoteDataSource : MoviesDataSource, KoinComponent {
    private val api: TmdbApi by inject()
    override suspend fun getGenres(apiKey: String, language: String): GenreResponse {
        return api.genres(apiKey, language)
    }

    override suspend fun getUpcomingMovies(
        apiKey: String,
        language: String,
        page: Long,
        region: String
    ): UpcomingMoviesResponse {
        return api.upcomingMovies(apiKey, language, page, region)
    }

    override suspend fun getsearchUpcomingMovies(
        apiKey: String,
        language: String,
        query: String,
        region: String
    ): UpcomingMoviesResponse {
        return api.searchUpcomingMovies(apiKey, language, query, region)
    }

    override suspend fun getMovie(id: Long, apiKey: String, language: String): Movie {
        return api.movie(id, apiKey, language)
    }

}