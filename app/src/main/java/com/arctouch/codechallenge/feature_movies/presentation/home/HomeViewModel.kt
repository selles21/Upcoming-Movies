package com.arctouch.codechallenge.feature_movies.presentation.home

import android.app.Application
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.Event
import com.arctouch.codechallenge.feature_movies.data.sources.remote.api.TmdbApi
import com.arctouch.codechallenge.feature_movies.data.Cache
import com.arctouch.codechallenge.feature_movies.data.sources.DefaultMovieRepository
import com.arctouch.codechallenge.feature_movies.domain.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.Exception

@KoinApiExtension
class HomeViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private var moviesWithGenres: ArrayList<Movie> = ArrayList()
    private var moviesWithGenresSearch: ArrayList<Movie> = ArrayList()
    private val defaultMovieRepository: DefaultMovieRepository by inject()

    private var _moviesLivedata = MutableLiveData<List<Movie>>()
    val moviesLivedata: LiveData<List<Movie>> = _moviesLivedata

    private var _totalLivedata = MutableLiveData<Event<Int>>()
    val totalLivedata: LiveData<Event<Int>> = _totalLivedata

    fun start() {
        if (moviesWithGenres.size == 0) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val upcomingMoviesResponse =
                        defaultMovieRepository.getUpcomingMovies(
                            BuildConfig.API_KEY,
                            TmdbApi.DEFAULT_LANGUAGE,
                            1,
                            TmdbApi.DEFAULT_REGION
                        )

                    moviesWithGenres.clear()
                    moviesWithGenres.addAll(upcomingMoviesResponse.results.map { movie ->
                        movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
                    })

                    withContext(Dispatchers.Main) {
                        _clearData.value = Event(true)
                        _totalLivedata.value = Event(upcomingMoviesResponse.totalPages)
                        _moviesLivedata.value = moviesWithGenres
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _moviesLivedata.value = arrayListOf()
                    }
                }
            }
        } else {
            _clearData.value = Event(true)
            _moviesLivedata.value = moviesWithGenres
        }
    }

    private var _clearData = MutableLiveData<Event<Boolean>>()
    val clearData: LiveData<Event<Boolean>> = _clearData

    private var _cachedGenres = MutableLiveData<Event<Boolean>>()
    val cachedGenres: LiveData<Event<Boolean>> = _cachedGenres

    fun loadGenresCache() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val genreResponse =
                    defaultMovieRepository.getGenres(BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
                Cache.cacheGenres(genreResponse.genres)
                withContext(Dispatchers.Main) {
                    _cachedGenres.value = Event(true)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {

                    _cachedGenres.value = Event(false)
                }
            }
        }
    }

    private var _openMovieLive = MutableLiveData<Event<Int>>()
    val openMovieLive: LiveData<Event<Int>> = _openMovieLive

    fun openMovie(id: Int) {
        _openMovieLive.value = Event(id)
    }

    fun onSearchClosed(): SearchView.OnCloseListener? {
        return SearchView.OnCloseListener {
            start()
            false
        }
    }

    fun onQueryTextChanged(searchView: SearchView): SearchView.OnQueryTextListener? {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true)
                }

                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                if (s.trim().isNotEmpty()) {
                    //Easy way filtering cached data.
//                _moviesLivedata.value = moviesWithGenres.filter {
//                    it.title.contains(s)
//                }

                    //Getting search from Api
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            val upcomingMoviesResponse =
                                defaultMovieRepository.getSearchUpcomingMovies(
                                    BuildConfig.API_KEY,
                                    TmdbApi.DEFAULT_LANGUAGE,
                                    s,
                                    TmdbApi.DEFAULT_REGION
                                )
                            moviesWithGenresSearch.clear()
                            moviesWithGenresSearch.addAll(upcomingMoviesResponse.results.map { movie ->
                                movie.copy(genres = Cache.genres.filter {
                                    movie.genreIds?.contains(
                                        it.id
                                    ) == true
                                })
                            })

                            withContext(Dispatchers.Main) {
                                _clearData.value = Event(true)
                                _moviesLivedata.value = moviesWithGenresSearch
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                moviesWithGenresSearch.clear()
                                _moviesLivedata.value = moviesWithGenresSearch
                            }
                        }
                    }
                } else {
                    start()
                }
                return false
            }
        }
    }

    fun nextPage(currentPage: Int) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val upcomingMoviesResponse =
                    defaultMovieRepository.getUpcomingMovies(
                        BuildConfig.API_KEY,
                        TmdbApi.DEFAULT_LANGUAGE,
                        currentPage.toLong(),
                        TmdbApi.DEFAULT_REGION
                    )

                moviesWithGenres = upcomingMoviesResponse.results.map { movie ->
                    movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
                } as ArrayList<Movie>

                withContext(Dispatchers.Main) {
                    _moviesLivedata.value = moviesWithGenres
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    moviesWithGenres = arrayListOf()
                    _moviesLivedata.value = moviesWithGenres
                }
            }
        }
    }

    fun snackListener(): View.OnClickListener? {
        return View.OnClickListener {
            loadGenresCache()
        }
    }


}