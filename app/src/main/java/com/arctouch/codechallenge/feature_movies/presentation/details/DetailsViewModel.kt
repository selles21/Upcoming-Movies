package com.arctouch.codechallenge.feature_movies.presentation.details

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.Event
import com.arctouch.codechallenge.feature_movies.data.sources.remote.api.TmdbApi
import com.arctouch.codechallenge.feature_movies.data.sources.DefaultMovieRepository
import com.arctouch.codechallenge.feature_movies.domain.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class DetailsViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val api: TmdbApi by inject()
    private val defaultMovieRepository: DefaultMovieRepository by inject()

    private var _movieLivedata = MutableLiveData<Event<Movie>>()
    val movieLivedata: LiveData<Event<Movie>> = _movieLivedata

    private var _movieFailed = MutableLiveData<Event<Boolean>>()
    val movieFailed: LiveData<Event<Boolean>> = _movieFailed

    fun start(id: Int) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val movie =
                    defaultMovieRepository.getMovie(
                        id.toLong(),
                        BuildConfig.API_KEY,
                        TmdbApi.DEFAULT_LANGUAGE
                    )
                withContext(Dispatchers.Main) {
                    _movieLivedata.value = Event(movie)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _movieFailed.value = Event(true)
                }
            }
        }
    }

    fun snackListener(id: Int): View.OnClickListener? {
        return View.OnClickListener {
            start(id)
        }
    }
}
