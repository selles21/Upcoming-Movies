package com.arctouch.codechallenge.details

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.model.Genre

@BindingAdapter("app:items")
fun setItems(recyclerView: RecyclerView, items: List<Genre>?) {
    items?.let { item ->
        (recyclerView.adapter as GenresAdapter).submitList(item.map { it.name })
    }
}