package com.hyeeyoung.wishboard.presentation.common.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.databinding.ItemGalleryImageBinding

class GalleryPagingAdapter(
    private val context: Context
) : PagingDataAdapter<Uri, RecyclerView.ViewHolder>(diffCallback) {
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(imageUri: Uri)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(private val binding: ItemGalleryImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUri: Uri) {
            Glide.with(context).load(imageUri).into(binding.itemImage)
            binding.container.setOnClickListener {
                listener.onItemClick(imageUri)
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
            is ViewHolder -> viewHolder.bind(imageUri)
        }
    }

    companion object {
        private const val TAG = "GalleryPagingAdapter"

        private val diffCallback = object : DiffUtil.ItemCallback<Uri>() {
            override fun areItemsTheSame(
                oldItem: Uri,
                newItem: Uri
            ): Boolean =
                if (oldItem is Uri && newItem is Uri) {
                    oldItem == newItem
                } else {
                    false
                }

            override fun areContentsTheSame(
                oldItem: Uri,
                newItem: Uri
            ): Boolean = oldItem == newItem
        }
    }
}