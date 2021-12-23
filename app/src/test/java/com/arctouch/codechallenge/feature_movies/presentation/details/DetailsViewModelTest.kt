package com.arctouch.codechallenge.feature_movies.presentation.details

import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.feature_movies.data.sources.remote.api.TmdbApi
import com.arctouch.codechallenge.feature_movies.data.sources.DefaultMovieRepository
import com.arctouch.codechallenge.di.appModule
import com.arctouch.codechallenge.di.networkModule
import com.arctouch.codechallenge.feature_movies.domain.model.Genre
import com.arctouch.codechallenge.feature_movies.domain.model.Movie
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.inject
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import retrofit2.HttpException


class DetailsViewModelTest : AutoCloseKoinTest() {

    private lateinit var mockedGenres: ArrayList<Genre>

    private lateinit var movie: Movie

    @Before
    fun setUptest() {
        val modulesList = listOf(networkModule, appModule)
        startKoin {
            modules(modulesList)
        }

        movie = Movie(4, "Film", "OverView", emptyList(), emptyList(), "/poster.png", "/backdrop.png", "2020-05-05")
    }

    @Test
    fun startWithApi() {
        val defaultMovieRepository: DefaultMovieRepository by inject()
        val test = defaultMovieRepository.getMovie(419704, BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE).test()

        test.awaitTerminalEvent()

        test.assertNoErrors()
    }

    @Test
    fun startWithWrongApi() {
        val defaultMovieRepository: DefaultMovieRepository by inject()
        val test = defaultMovieRepository.getMovie(419704, "sdlkfsldkf", TmdbApi.DEFAULT_LANGUAGE).test()

        test.awaitTerminalEvent()

        test.assertError(HttpException::class.java)
    }

    @Test
    fun startWithWrongId() {
        val defaultMovieRepository: DefaultMovieRepository by inject()
        val test = defaultMovieRepository.getMovie(1, BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE).test()

        test.awaitTerminalEvent()

        test.assertError(HttpException::class.java)
    }

    @Test
    fun startWithData() {

        val localDefaultMovieRepository = mock(DefaultMovieRepository::class.java)

        val observable = Observable.just(movie)

        `when`(localDefaultMovieRepository.getMovie(1, BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE)).thenReturn(observable)

        val test = localDefaultMovieRepository.getMovie(1, BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE).test()

        test.awaitTerminalEvent()

        test.assertValue(movie)
    }

    @After
    fun finish() {
        stopKoin()
    }

}