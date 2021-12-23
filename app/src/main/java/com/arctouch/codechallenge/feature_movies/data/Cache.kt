package com.arctouch.codechallenge.feature_movies.data

import com.arctouch.codechallenge.feature_movies.domain.model.Genre

object Cache {

    var genres = listOf<Genre>()

    fun cacheGenres(genres: List<Genre>) {
        Cache.genres = genres
    }
}
