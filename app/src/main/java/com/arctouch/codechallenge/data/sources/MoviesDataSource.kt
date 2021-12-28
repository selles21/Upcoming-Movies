package com.arctouch.codechallenge.data.sources

import com.arctouch.codechallenge.model.GenreResponse
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesResponse
import io.reactivex.Observable

interface MoviesDataSource {

    fun getGenres(apiKey: String, language: String): Observable<GenreResponse>

    fun getUpcomingMovies(apiKey: String, language: String, page: Long, region: String): Observable<UpcomingMoviesResponse>

    fun getsearchUpcomingMovies(apiKey: String, language: String, query: String, region: String): Observable<UpcomingMoviesResponse>

    fun getMovie(id: Long, apiKey: String, language: String): Observable<Movie>
}