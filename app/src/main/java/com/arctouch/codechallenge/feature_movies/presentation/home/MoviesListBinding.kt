package com.arctouch.codechallenge.feature_movies.presentation.home

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.feature_movies.domain.model.Genre
import com.arctouch.codechallenge.feature_movies.domain.util.MovieImageUrlBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("app:genres")
fun setItems(textView: TextView, items: List<Genre>?) {
    textView.text = items?.joinToString(separator = ", ") { it.name }
}

@BindingAdapter("imageUrlPoster")
fun ImageView.setImageUrl(url: String?) {
    val movieImageUrlBuilder: MovieImageUrlBuilder = MovieImageUrlBuilder()
    Glide.with(context)
            .load(url?.let { movieImageUrlBuilder.buildPosterUrl(it) })
            .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
            .into(this)
}

@BindingAdapter("imageUrlBackdrop")
fun ImageView.setImageUrlBackdrop(url: String?) {
    val movieImageUrlBuilder: MovieImageUrlBuilder = MovieImageUrlBuilder()
    Glide.with(context)
            .load(url?.let { movieImageUrlBuilder.buildBackdropUrl(it) })
            .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
            .into(this)
}