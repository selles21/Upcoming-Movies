package com.arctouch.codechallenge.feature_movies.data.sources

import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.feature_movies.data.Cache
import com.arctouch.codechallenge.feature_movies.data.Result
import com.arctouch.codechallenge.feature_movies.data.sources.remote.MoviesRemoteDataSource
import com.arctouch.codechallenge.feature_movies.data.sources.remote.api.TmdbApi
import com.arctouch.codechallenge.feature_movies.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.Exception

private const val GITHUB_STARTING_PAGE_INDEX = 1

@KoinApiExtension
class DefaultMovieRepository : KoinComponent {
    private var isRequestInProgress: Boolean = false

    private val moviesRemoteDataSource: MoviesRemoteDataSource by inject()

    private val searchResults = MutableSharedFlow<Result<Any>>(replay = 1)
    private var lastRequestedPage = GITHUB_STARTING_PAGE_INDEX

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

    suspend fun getGenres(apiKey: String, language: String): Flow<Result<Any>> {
        try {
            val genreResponse = moviesRemoteDataSource.getGenres(apiKey, language)
            Cache.cacheGenres(genreResponse.genres)

            val upcomingMovies = getUpcomingMovies(
                BuildConfig.API_KEY,
                TmdbApi.DEFAULT_LANGUAGE,
                1,
                TmdbApi.DEFAULT_REGION
            )
        } catch (e: Exception) {
            searchResults.emit(Result.Error(e))
        }

        return searchResults
    }

    suspend fun getUpcomingMovies(
        apiKey: String,
        language: String,
        page: Long,
        region: String,
        query: String = ""
    ) {
        isRequestInProgress = true
        try {
            if (query.isEmpty()) {
                val response =
                    moviesRemoteDataSource.getUpcomingMovies(apiKey, language, page, region)
                        .results.map { movie ->
                            movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
                        } as ArrayList<Movie>
                searchResults.emit(Result.Success(response))

            } else {
                val searchUpcomingMovies =
                    moviesRemoteDataSource.getsearchUpcomingMovies(apiKey, language, query, region)
                        .results.map { movie ->
                            movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
                        } as ArrayList<Movie>
                searchResults.emit(Result.Success(searchUpcomingMovies))

            }

        } catch (e: Exception) {
            searchResults.emit(Result.Error(e))
        }

        isRequestInProgress = false
//        return moviesRemoteDataSource.getUpcomingMovies(apiKey, language, page, region)
    }

    suspend fun getSearchUpcomingMovies(
        apiKey: String,
        language: String,
        query: String,
        region: String
    ): Flow<Result<Any>> {

        getSearchUpcomingMoviesResponse(
            apiKey,
            language,
            query,
            region
        )

        return searchResults
//        return moviesRemoteDataSource.getsearchUpcomingMovies(apiKey, language, query, region)
    }

    private suspend fun getSearchUpcomingMoviesResponse(
        apiKey: String,
        language: String,
        query: String,
        region: String
    ) {
        try {
            val searchUpcomingMovies =
                moviesRemoteDataSource.getsearchUpcomingMovies(apiKey, language, query, region)
                    .results.map { movie ->
                        movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
                    } as ArrayList<Movie>
            searchResults.emit(Result.Success(searchUpcomingMovies))
        } catch (e: Exception) {
            searchResults.emit(Result.Error(e))
        }
    }

    suspend fun getMovie(id: Long, apiKey: String, language: String): Movie {
        return moviesRemoteDataSource.getMovie(id, apiKey, language)
    }
}