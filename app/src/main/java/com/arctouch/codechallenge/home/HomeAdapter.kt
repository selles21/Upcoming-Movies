package com.arctouch.codechallenge.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.databinding.MovieItemBinding
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.MovieImageUrlBuilder

class HomeAdapter(private val viewModel: HomeViewModel, private val movies: ArrayList<Movie>) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    class ViewHolder(val itemBinding: MovieItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

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

    fun addItems(items: ArrayList<Movie>) {
        movies.addAll(items)
        notifyItemRangeInserted(movies.size - items.size, items.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount() = movies.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(viewModel, movies[position])
}
