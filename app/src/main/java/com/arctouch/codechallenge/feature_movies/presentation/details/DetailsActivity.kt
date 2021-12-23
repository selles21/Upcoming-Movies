package com.arctouch.codechallenge.feature_movies.presentation.details

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.arctouch.codechallenge.EventObserver
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.databinding.ActivityDetailsBinding

import kotlinx.android.synthetic.main.activity_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsActivity : AppCompatActivity() {

    private var id: Int? = 0
    private lateinit var genresAdapter: GenresAdapter
    private val detailViewModel: DetailsViewModel by viewModel()
    private lateinit var activityDetailsBinding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_details)
        activityDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details)
//        activityDetailsBinding = ActivityDetailsBinding.inflate(layoutInflater)
        activityDetailsBinding.lifecycleOwner = this
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        setupListGenres()
        observeData()

        id = intent?.extras?.getInt("id")

        id?.let {
            detailViewModel.start(it)
        }

    }

    private fun setupListGenres() {
        genresAdapter = GenresAdapter()
        activityDetailsBinding.rvGenres.adapter = genresAdapter
    }

    private fun observeData() {


        detailViewModel.movieFailed.observe(this, EventObserver {
            if (it)
                id?.let {
                    Snackbar.make(activityDetailsBinding.rvGenres, "Unable to reach the data.", Snackbar.LENGTH_INDEFINITE)
                            .setActionTextColor(Color.WHITE)
                            .setAction("Try again", detailViewModel.snackListener(it)).show()
                }

        })

        detailViewModel.movieLivedata.observe(this, EventObserver {
            supportActionBar?.title = it.title
            activityDetailsBinding.movie = it
            activityDetailsBinding.progressBar.visibility = View.GONE
            activityDetailsBinding.executePendingBindings()
        })

    }

}
