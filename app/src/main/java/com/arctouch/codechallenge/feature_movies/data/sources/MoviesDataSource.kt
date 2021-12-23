package com.arctouch.codechallenge.feature_movies.data.sources

import com.arctouch.codechallenge.feature_movies.domain.model.GenreResponse
import com.arctouch.codechallenge.feature_movies.domain.model.Movie
import com.arctouch.codechallenge.feature_movies.domain.model.UpcomingMoviesResponse

interface MoviesDataSource {

    suspend fun getGenres(apiKey: String, language: String): GenreResponse

    suspend fun getUpcomingMovies(
        apiKey: String,
        language: String,
        page: Long,
        region: String
    ): UpcomingMoviesResponse

    suspend fun getsearchUpcomingMovies(
        apiKey: String,
        language: String,
        query: String,
        region: String
    ): UpcomingMoviesResponse

    suspend fun getMovie(id: Long, apiKey: String, language: String): Movie
}