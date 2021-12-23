package com.arctouch.codechallenge.di

import com.arctouch.codechallenge.feature_movies.data.sources.remote.api.TmdbApi
import com.arctouch.codechallenge.feature_movies.data.sources.remote.MoviesRemoteDataSource
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


val networkModule = module {
    factory { provideOkHttpClient() }
    factory { provideForecastApi(get()) }
    single { provideRetrofit(get()) }
    single { MoviesRemoteDataSource() }


}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
            .baseUrl(TmdbApi.URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
}

fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().build()
}

fun provideForecastApi(retrofit: Retrofit): TmdbApi = retrofit.create(TmdbApi::class.java)