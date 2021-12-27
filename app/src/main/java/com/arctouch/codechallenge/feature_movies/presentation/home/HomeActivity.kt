package com.arctouch.codechallenge.feature_movies.presentation.home

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.arctouch.codechallenge.EventObserver
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.databinding.HomeActivityBinding
import com.arctouch.codechallenge.feature_movies.data.Result
import com.arctouch.codechallenge.feature_movies.presentation.details.DetailsActivity
import com.arctouch.codechallenge.feature_movies.domain.model.Movie
import com.arctouch.codechallenge.feature_movies.domain.util.EndlessRecyclerOnScrollListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.home_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension


@KoinApiExtension
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

        // bind the state
        homeActivityBinding.bindState(
            uiState = homeViewModel.state,
            uiActions = homeViewModel.accept
        )

//        setupRecyclerView(homeAdapter)
//        homeViewModel.loadGenresCache()
//        observeData()
    }

    private fun HomeActivityBinding.bindState(
        uiState: LiveData<UiState>,
        uiActions: (UiAction) -> Unit
    ) {
        val homeAdapter = HomeAdapter(homeViewModel, arrayListOf<Movie>())
        setupRecyclerView(homeAdapter)

        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            repoAdapter = homeAdapter,
            uiState = uiState,
            onScrollChanged = uiActions
        )

    }

    private fun HomeActivityBinding.bindSearch(
        uiState: LiveData<UiState>,
        onQueryChanged: (UiAction) -> Unit
    ) {

    }

    private fun HomeActivityBinding.bindList(
        repoAdapter: HomeAdapter,
        uiState: LiveData<UiState>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        uiState
            .map(UiState::searchResult)
            .distinctUntilChanged()
            .observe(this@HomeActivity) { result ->
                when (result) {
                    is Result.Success -> {
                        showEmptyList((result.data as List<*>).isEmpty())
                        repoAdapter.submitList(result.data as List<Movie>)
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            this@HomeActivity,
                            "\uD83D\uDE28 Wooops $result.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }

    private fun HomeActivityBinding.showEmptyList(show: Boolean) {
        emptyList.isVisible = show
        recyclerView.isVisible = !show
        progressBar.isVisible = false
    }

    private fun setupRecyclerView(homeAdapter: HomeAdapter) {
        recyclerView.adapter = homeAdapter
        mScrollListener = object :
            EndlessRecyclerOnScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
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

//            homeAdapter.addItems(it as ArrayList<Movie>)
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
//                setupRecyclerView(homeAdapter)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setOnQueryTextListener(onQueryTextChanged(this))
            setOnCloseListener(onSearchClosed())
        }

        return true
    }

    fun onSearchClosed(): SearchView.OnCloseListener? {
        return SearchView.OnCloseListener {
            clearSearch(homeViewModel.accept)
            false
        }
    }

    private fun clearSearch(accept: (UiAction) -> Unit) {

    }

    private fun onQueryTextChanged(searchView: SearchView): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!searchView.isIconified) {
                    searchView.isIconified = true
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                if (newText?.trim()?.isNotEmpty() == true) {
                homeActivityBinding.updateRepoListFromInput(homeViewModel.accept, newText)
//                }
                return false
            }
        }
    }

    private fun HomeActivityBinding.updateRepoListFromInput(
        onQueryChanged: (UiAction.Search) -> Unit,
        newText: String?
    ) {
        newText?.trim().let {
//            if (it.isNotEmpty()) {
            recyclerView.scrollToPosition(0)
            onQueryChanged(UiAction.Search(query = it ?: ""))
//            }
        }
    }

}
