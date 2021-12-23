package com.arctouch.codechallenge.di

import com.arctouch.codechallenge.feature_movies.data.sources.DefaultMovieRepository
import com.arctouch.codechallenge.feature_movies.presentation.details.DetailsViewModel
import com.arctouch.codechallenge.feature_movies.presentation.home.HomeViewModel
import com.arctouch.codechallenge.feature_movies.domain.util.MovieImageUrlBuilder
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { DefaultMovieRepository.getRepository() }

    viewModel {
        HomeViewModel(androidApplication())
    }

    viewModel {
        DetailsViewModel(androidApplication())
    }

    single {
        MovieImageUrlBuilder()
    }
}