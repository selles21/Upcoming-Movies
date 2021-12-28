package com.arctouch.codechallenge.home

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.Event
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.data.Cache
import com.arctouch.codechallenge.data.sources.DefaultMovieRepository
import com.arctouch.codechallenge.model.Movie
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

class HomeViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private var moviesWithGenres: ArrayList<Movie> = ArrayList()
    private var moviesWithGenresSearch: ArrayList<Movie> = ArrayList()
    private val defaultMovieRepository: DefaultMovieRepository by inject()

    private var _moviesLivedata = MutableLiveData<List<Movie>>()
    val moviesLivedata: LiveData<List<Movie>> = _moviesLivedata

    private var _totalLivedata = MutableLiveData<Event<Int>>()
    val totalLivedata: LiveData<Event<Int>> = _totalLivedata

    fun start() {
        if (moviesWithGenres.size == 0)
            defaultMovieRepository.getUpcomingMovies(BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE, 1, TmdbApi.DEFAULT_REGION)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        moviesWithGenres.clear()
                        moviesWithGenres.addAll(it.results.map { movie ->
                            movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
                        })
                        _clearData.value = Event(true)
                        _totalLivedata.value = Event(it.totalPages)
                        _moviesLivedata.value = moviesWithGenres
                    }, {
                        _moviesLivedata.value = arrayListOf()
                    })
        else {
            _clearData.value = Event(true)
            _moviesLivedata.value = moviesWithGenres
        }
    }

    private var _clearData = MutableLiveData<Event<Boolean>>()
    val clearData: LiveData<Event<Boolean>> = _clearData

    private var _cachedGenres = MutableLiveData<Event<Boolean>>()
    val cachedGenres: LiveData<Event<Boolean>> = _cachedGenres

    fun loadGenresCache() {
        defaultMovieRepository.getGenres(BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Cache.cacheGenres(it.genres)
                    _cachedGenres.value = Event(true)
                }, {
                    _cachedGenres.value = Event(false)
                })
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
                    defaultMovieRepository.getSearchUpcomingMovies(BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE, s, TmdbApi.DEFAULT_REGION)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                moviesWithGenresSearch.clear()
                                moviesWithGenresSearch.addAll(it.results.map { movie ->
                                    movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
                                })
                                _clearData.value = Event(true)
                                _moviesLivedata.value = moviesWithGenresSearch
                            }, {
                                moviesWithGenresSearch.clear()
                                _moviesLivedata.value = moviesWithGenresSearch
                            })
                } else {
                    start()
                }
                return false
            }
        }
    }

    fun nextPage(currentPage: Int) {
        defaultMovieRepository.getUpcomingMovies(BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE, currentPage.toLong(), TmdbApi.DEFAULT_REGION)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            moviesWithGenres = it.results.map { movie ->
                                movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
                            } as ArrayList<Movie>
                            _moviesLivedata.value = moviesWithGenres
                        },
                        {
                            moviesWithGenres = arrayListOf()
                            _moviesLivedata.value = moviesWithGenres
                        }
                )
    }

    fun snackListener(): View.OnClickListener? {
        return View.OnClickListener {
            loadGenresCache()
        }
    }


}