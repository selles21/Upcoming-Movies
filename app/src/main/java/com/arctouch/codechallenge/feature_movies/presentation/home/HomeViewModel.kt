package com.arctouch.codechallenge.feature_movies.presentation.home

import android.app.Application
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.*
import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.Event
import com.arctouch.codechallenge.feature_movies.data.sources.remote.api.TmdbApi
import com.arctouch.codechallenge.feature_movies.data.Cache
import com.arctouch.codechallenge.feature_movies.data.Result
import com.arctouch.codechallenge.feature_movies.data.sources.DefaultMovieRepository
import com.arctouch.codechallenge.feature_movies.domain.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
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

    /**
     * Stream of immutable states representative of the UI.
     */
    val state: LiveData<UiState>


    /**
     * Processor of side effects from the UI which in turn feedback into [state]
     */
    val accept: (UiAction) -> Unit

    init {
        val queryLiveData =
            MutableLiveData(DEFAULT_QUERY)

        state = queryLiveData
            .distinctUntilChanged()
            .switchMap { queryString ->
                liveData {
                    val uiState = when {
                        queryString.isEmpty() -> defaultMovieRepository.getGenres(
                            BuildConfig.API_KEY,
                            TmdbApi.DEFAULT_LANGUAGE
                        )
                            .map {
                                UiState(
                                    query = "",
                                    searchResult = it
                                )
                            }
                            .asLiveData(Dispatchers.Main)

                        else -> defaultMovieRepository.getSearchUpcomingMovies(
                            BuildConfig.API_KEY,
                            TmdbApi.DEFAULT_LANGUAGE,
                            queryString,
                            TmdbApi.DEFAULT_REGION
                        )
                            .map {
                                UiState(
                                    query = queryString,
                                    searchResult = it
                                )
                            }
                            .asLiveData(Dispatchers.Main)
                    }
                    defaultMovieRepository.getGenres(BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
                        .map {
                            UiState(
                                query = "",
                                searchResult = it
                            )
                        }
                        .asLiveData(Dispatchers.Main)
                    emitSource(uiState)
                }
            }

        accept = { action ->
            when (action) {
                is UiAction.Search -> queryLiveData.postValue(action.query)
                is UiAction.Scroll -> if (action.shouldFetchMore) {
                    val immutableQuery = queryLiveData.value
                    if (immutableQuery != null) {
                        viewModelScope.launch {
                            defaultMovieRepository.getUpcomingMovies(
                                BuildConfig.API_KEY,
                                TmdbApi.DEFAULT_LANGUAGE,
                                action.lastPage.toLong(),
                                TmdbApi.DEFAULT_REGION
                            )
                        }
                    }
                }
            }
        }

    }

    fun start() {
//        if (moviesWithGenres.size == 0) {
//            viewModelScope.launch(Dispatchers.IO) {
//                try {
//                    val upcomingMoviesResponse =
//                        defaultMovieRepository.getUpcomingMovies(
//                            BuildConfig.API_KEY,
//                            TmdbApi.DEFAULT_LANGUAGE,
//                            1,
//                            TmdbApi.DEFAULT_REGION
//                        )
//
//                    moviesWithGenres.clear()
//                    moviesWithGenres.addAll(upcomingMoviesResponse.results.map { movie ->
//                        movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
//                    })
//
//                    withContext(Dispatchers.Main) {
//                        _clearData.value = Event(true)
//                        _totalLivedata.value = Event(upcomingMoviesResponse.totalPages)
//                        _moviesLivedata.value = moviesWithGenres
//                    }
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) {
//                        _moviesLivedata.value = arrayListOf()
//                    }
//                }
//            }
//        } else {
//            _clearData.value = Event(true)
//            _moviesLivedata.value = moviesWithGenres
//        }
    }

    private var _clearData = MutableLiveData<Event<Boolean>>()
    val clearData: LiveData<Event<Boolean>> = _clearData

    private var _cachedGenres = MutableLiveData<Event<Boolean>>()
    val cachedGenres: LiveData<Event<Boolean>> = _cachedGenres

    fun loadGenresCache() {
//        state = liveData {
//            val uiState =
//                defaultMovieRepository.getGenres(BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
//                    .map {
//                        UiState(
//                            query = "",
//                            searchResult = it
//                        )
//                    }
//                    .asLiveData(Dispatchers.Main)
//            emitSource(uiState)
//        }
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
//                if (!searchView.isIconified) {
//                    searchView.isIconified = true
//                }

                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
//                if (s.trim().isNotEmpty()) {

                //Getting search from Api
//                    viewModelScope.launch(Dispatchers.IO) {
//                        try {
//                            val upcomingMoviesResponse =
//                                defaultMovieRepository.getSearchUpcomingMovies(
//                                    BuildConfig.API_KEY,
//                                    TmdbApi.DEFAULT_LANGUAGE,
//                                    s,
//                                    TmdbApi.DEFAULT_REGION
//                                )
//                            moviesWithGenresSearch.clear()
//                            moviesWithGenresSearch.addAll(upcomingMoviesResponse.results.map { movie ->
//                                movie.copy(genres = Cache.genres.filter {
//                                    movie.genreIds?.contains(
//                                        it.id
//                                    ) == true
//                                })
//                            })
//
//                            withContext(Dispatchers.Main) {
//                                _clearData.value = Event(true)
//                                _moviesLivedata.value = moviesWithGenresSearch
//                            }
//                        } catch (e: Exception) {
//                            withContext(Dispatchers.Main) {
//                                moviesWithGenresSearch.clear()
//                                _moviesLivedata.value = moviesWithGenresSearch
//                            }
//                        }
//                    }
//                } else {
//                    start()
//                }
                return false
            }
        }
    }

    fun nextPage(currentPage: Int) {

//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val upcomingMoviesResponse =
//                    defaultMovieRepository.getUpcomingMovies(
//                        BuildConfig.API_KEY,
//                        TmdbApi.DEFAULT_LANGUAGE,
//                        currentPage.toLong(),
//                        TmdbApi.DEFAULT_REGION
//                    )
//
//                moviesWithGenres = upcomingMoviesResponse.results.map { movie ->
//                    movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
//                } as ArrayList<Movie>
//
//                withContext(Dispatchers.Main) {
//                    _moviesLivedata.value = moviesWithGenres
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    moviesWithGenres = arrayListOf()
//                    _moviesLivedata.value = moviesWithGenres
//                }
//            }
//        }
    }

    fun snackListener(): View.OnClickListener? {
        return View.OnClickListener {
            loadGenresCache()
        }
    }


}

private val UiAction.Scroll.shouldFetchMore
    get() = visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount

sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(
        val lastPage: Int,
        val visibleItemCount: Int,
        val lastVisibleItemPosition: Int,
        val totalItemCount: Int
    ) : UiAction()
}

data class UiState(
    val query: String,
    val searchResult: Result<Any>
)

private const val VISIBLE_THRESHOLD = 5
private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val DEFAULT_QUERY = ""