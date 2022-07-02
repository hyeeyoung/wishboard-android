package com.hyeeyoung.wishboard.presentation.common.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hyeeyoung.wishboard.data.model.GalleryData
import com.hyeeyoung.wishboard.databinding.ItemGalleryImageBinding

class GalleryPagingAdapter(private val onItemClick: (Uri) -> Unit) :
    PagingDataAdapter<GalleryData, RecyclerView.ViewHolder>(diffCallback) {
    class ViewHolder(private val binding: ItemGalleryImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUri: GalleryData, onItemClick: (Uri) -> Unit) {
            with(binding) {
                itemImage.load(imageUri.uri)
                container.setOnClickListener {
                    onItemClick(imageUri.uri)
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemGalleryImageBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val imageUri = getItem(position) ?: return
        when (viewHolder) {
            is ViewHolder -> viewHolder.bind(imageUri, onItemClick)
        }
    }

    companion object {
        private const val TAG = "GalleryPagingAdapter"

        private val diffCallback = object : DiffUtil.ItemCallback<GalleryData>() {
            override fun areItemsTheSame(
                oldItem: GalleryData,
                newItem: GalleryData
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: GalleryData,
                newItem: GalleryData
            ): Boolean = oldItem == newItem
        }
    }
}