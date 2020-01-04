package com.arctouch.codechallenge.home

import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.data.sources.DefaultMovieRepository
import com.arctouch.codechallenge.di.appModule
import com.arctouch.codechallenge.di.networkModule
import com.arctouch.codechallenge.model.Genre
import com.arctouch.codechallenge.model.GenreResponse
import io.reactivex.Observable
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.inject
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito
import retrofit2.HttpException

class HomeViewModelTest : AutoCloseKoinTest() {

    private lateinit var genreResponse: GenreResponse
    private lateinit var mockedGenres: ArrayList<Genre>

    @Before
    fun setUptest() {
        val modulesList = listOf(networkModule, appModule)
        startKoin {
            modules(modulesList)
        }
        val genre1 = Genre(1, "Terror")
        val genre2 = Genre(2, "Fantasia")
        val genre3 = Genre(3, "Drama")
        val genre4 = Genre(4, "Aventura")

        mockedGenres = arrayListOf(genre1, genre2, genre3, genre4)
        genreResponse = GenreResponse(mockedGenres)
    }

    @Test
    fun genreWithData() {

        val localDefaultMovieRepository = Mockito.mock(DefaultMovieRepository::class.java)

        val observable = Observable.just(genreResponse)

        Mockito.`when`(localDefaultMovieRepository.getGenres(BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE)).thenReturn(observable)

        val test = localDefaultMovieRepository.getGenres(BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE).test()

        test.awaitTerminalEvent()

        test.assertValue(genreResponse).assertNoErrors()
    }

    @Test
    fun genresWithApi() {
        val defaultMovieRepository: DefaultMovieRepository by inject()
        val test = defaultMovieRepository.getGenres(BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE).test()

        test.awaitTerminalEvent()

        test.assertNoErrors()
    }

    @Test
    fun genresWithWrongApi() {
        val defaultMovieRepository: DefaultMovieRepository by inject()
        val test = defaultMovieRepository.getGenres("assdf", TmdbApi.DEFAULT_LANGUAGE).test()

        test.awaitTerminalEvent()

        test.assertError(HttpException::class.java)
    }

    @Test
    fun searchMovieEmptyQuery() {
        val defaultMovieRepository: DefaultMovieRepository by inject()
        val test = defaultMovieRepository.getSearchUpcomingMovies(BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE, "", TmdbApi.DEFAULT_REGION).test()

        test.awaitTerminalEvent()

        test.assertError(HttpException::class.java)
    }

    @Test
    fun searchMovieQuery() {
        val defaultMovieRepository: DefaultMovieRepository by inject()
        val test = defaultMovieRepository.getSearchUpcomingMovies(BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE, "a", TmdbApi.DEFAULT_REGION).test()

        test.awaitTerminalEvent()

        test.assertNoErrors()
    }

    @After
    fun finish() {
        stopKoin()
    }
}