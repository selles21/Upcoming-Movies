package com.arctouch.codechallenge.feature_movies.presentation.home

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.arctouch.codechallenge.EventObserver
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.databinding.HomeActivityBinding
import com.arctouch.codechallenge.feature_movies.presentation.details.DetailsActivity
import com.arctouch.codechallenge.feature_movies.domain.model.Movie
import com.arctouch.codechallenge.feature_movies.domain.util.EndlessRecyclerOnScrollListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.home_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeActivity : AppCompatActivity() {
    private lateinit var mScrollListener: EndlessRecyclerOnScrollListener
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var homeActivityBinding: HomeActivityBinding
    private var totalPages: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        homeActivityBinding = HomeActivityBinding.inflate(layoutInflater)
        homeActivityBinding.lifecycleOwner = this
        setupRecyclerView()
        homeViewModel.loadGenresCache()
        observeData()
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = HomeAdapter(homeViewModel, arrayListOf<Movie>())
        mScrollListener = object : EndlessRecyclerOnScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(current_page: Int) {
                homeViewModel.nextPage(current_page)
//                if (current_page == totalPages)
                setLoading(false)
            }
        }
        recyclerView.addOnScrollListener(mScrollListener)
    }

    private fun observeData() {
        homeViewModel.moviesLivedata.observe(this, Observer {
            val homeAdapter = recyclerView.adapter as HomeAdapter

            homeAdapter.addItems(it as ArrayList<Movie>)
            if (homeAdapter.itemCount != it.size)
                recyclerView.scrollToPosition(homeAdapter.itemCount - (it.size - 1))
            mScrollListener.setLoading(false)
            progressBar.visibility = View.GONE
        })

        homeViewModel.openMovieLive.observe(this, EventObserver {

            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("id", it)
            startActivity(intent)
        })

        homeViewModel.totalLivedata.observe(this, EventObserver {
            totalPages = it
        })

        homeViewModel.cachedGenres.observe(this, EventObserver {
            if (it)
                homeViewModel.start()
            else {
                Snackbar.make(recyclerView, "Unable to reach the data.", Snackbar.LENGTH_INDEFINITE)
                        .setActionTextColor(Color.WHITE)
                        .setAction("Try again", homeViewModel.snackListener()).show()
            }
        })
        homeViewModel.clearData.observe(this, EventObserver {
            if (it) {
                setupRecyclerView()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setOnQueryTextListener(homeViewModel.onQueryTextChanged(this))
            setOnCloseListener(homeViewModel.onSearchClosed())
        }

        return true
    }

}
