package com.arctouch.codechallenge.feature_movies.data.sources.remote.api

import com.arctouch.codechallenge.feature_movies.domain.model.GenreResponse
import com.arctouch.codechallenge.feature_movies.domain.model.Movie
import com.arctouch.codechallenge.feature_movies.domain.model.UpcomingMoviesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    companion object {
        const val URL = "https://api.themoviedb.org/3/"
        const val DEFAULT_LANGUAGE = "pt-BR"
        const val DEFAULT_REGION = "BR"
    }

    @GET("genre/movie/list")
    suspend fun genres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): GenreResponse

    @GET("movie/upcoming")
    suspend fun upcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Long,
        @Query("region") region: String
    ): UpcomingMoviesResponse

    @GET("movie/{id}")
    suspend fun movie(
        @Path("id") id: Long,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Movie

    @GET("search/movie")
    suspend fun searchUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("query") query: String,
        @Query("region") region: String
    ): UpcomingMoviesResponse
}
