package com.arctouch.codechallenge.details

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.Event
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.data.sources.DefaultMovieRepository
import com.arctouch.codechallenge.model.Movie
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

class DetailsViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val api: TmdbApi by inject()
    private val defaultMovieRepository: DefaultMovieRepository by inject()

    private var _movieLivedata = MutableLiveData<Event<Movie>>()
    val movieLivedata: LiveData<Event<Movie>> = _movieLivedata

    private var _movieFailed = MutableLiveData<Event<Boolean>>()
    val movieFailed: LiveData<Event<Boolean>> = _movieFailed

    fun start(id: Int) {
        defaultMovieRepository.getMovie(id.toLong(), BuildConfig.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ movie ->
                    _movieLivedata.value = Event(movie)
                }, {
                    _movieFailed.value = Event(true)
                })
    }

    fun snackListener(id: Int): View.OnClickListener? {
        return View.OnClickListener {
            start(id)
        }
    }
}
