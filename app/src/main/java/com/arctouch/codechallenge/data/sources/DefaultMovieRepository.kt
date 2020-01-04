package com.arctouch.codechallenge.data.sources

import android.app.Application
import com.arctouch.codechallenge.data.sources.remote.MoviesRemoteDataSource
import com.arctouch.codechallenge.model.GenreResponse
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesResponse
import io.reactivex.Observable
import org.koin.core.KoinComponent
import org.koin.core.inject

class DefaultMovieRepository() : KoinComponent {
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

    fun getGenres(apiKey: String, language: String): Observable<GenreResponse> {
        return moviesRemoteDataSource.getGenres(apiKey, language)
    }

    fun getUpcomingMovies(apiKey: String, language: String, page: Long, region: String): Observable<UpcomingMoviesResponse> {
        return moviesRemoteDataSource.getUpcomingMovies(apiKey, language, page, region)
    }

    fun getSearchUpcomingMovies(apiKey: String, language: String, query: String, region: String): Observable<UpcomingMoviesResponse> {
        return moviesRemoteDataSource.getsearchUpcomingMovies(apiKey, language, query, region)
    }

    fun getMovie(id: Long, apiKey: String, language: String): Observable<Movie> {
        return moviesRemoteDataSource.getMovie(id, apiKey, language)
    }
}