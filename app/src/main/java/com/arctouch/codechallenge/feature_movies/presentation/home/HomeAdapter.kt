package com.arctouch.codechallenge.feature_movies.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.databinding.MovieItemBinding
import com.arctouch.codechallenge.feature_movies.domain.model.Movie
import com.arctouch.codechallenge.feature_movies.domain.util.MovieImageUrlBuilder
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class HomeAdapter(private val viewModel: HomeViewModel, private val movies: ArrayList<Movie>) :
    ListAdapter<Movie, HomeAdapter.ViewHolder>(MOVIE_COMPARATOR) {

    class ViewHolder(val itemBinding: MovieItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private val movieImageUrlBuilder = MovieImageUrlBuilder()

        fun bind(viewModel: HomeViewModel, movie: Movie) {

            itemBinding.movie = movie
            itemBinding.handler = viewModel
            itemBinding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MovieItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = getItem(position)
        if (movie != null) {
            holder.bind(viewModel, movie)
        }
    }

    companion object {
        private val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
                oldItem == newItem
        }
    }
}
