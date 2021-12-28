package com.arctouch.codechallenge.data.sources.remote

import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.data.sources.MoviesDataSource
import com.arctouch.codechallenge.model.GenreResponse
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesResponse
import io.reactivex.Observable
import org.koin.core.KoinComponent
import org.koin.core.inject

class MoviesRemoteDataSource : MoviesDataSource, KoinComponent {
    private val api: TmdbApi by inject()
    override fun getGenres(apiKey: String, language: String): Observable<GenreResponse> {
        return api.genres(apiKey, language)
    }

    override fun getUpcomingMovies(apiKey: String, language: String, page: Long, region: String): Observable<UpcomingMoviesResponse> {
        return api.upcomingMovies(apiKey, language, page, region)
    }

    override fun getsearchUpcomingMovies(apiKey: String, language: String, query: String, region: String): Observable<UpcomingMoviesResponse> {
        return api.searchUpcomingMovies(apiKey, language, query, region)
    }

    override fun getMovie(id: Long, apiKey: String, language: String): Observable<Movie> {
        return api.movie(id, apiKey, language)
    }

}