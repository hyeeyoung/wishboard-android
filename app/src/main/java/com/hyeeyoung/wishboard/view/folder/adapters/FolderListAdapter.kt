package com.hyeeyoung.wishboard.view.folder.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hyeeyoung.wishboard.databinding.ItemFolderHorizontalBinding
import com.hyeeyoung.wishboard.databinding.ItemFolderSquareBinding
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.model.folder.FolderListViewType
import com.hyeeyoung.wishboard.util.ImageLoader

class FolderListAdapter(
    private val folderListViewType: FolderListViewType,
) : ListAdapter<FolderItem, RecyclerView.ViewHolder>(diffCallback) {
    private val dataSet = arrayListOf<FolderItem>()
    private lateinit var listener: OnItemClickListener
    private var folderMoreDialogListener: OnFolderMoreDialogListener? = null
    private lateinit var imageLoader: ImageLoader
    private var selectedFolderPosition: Int? = null

    init {
        setHasStableIds(true)
    }

    interface OnItemClickListener {
        fun onItemClick(item: FolderItem)
    }

    interface OnFolderMoreDialogListener {
        fun onItemMoreButtonClick(position: Int, item: FolderItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setOnFolderMoreDialogListener(listener: OnFolderMoreDialogListener) {
        this.folderMoreDialogListener = listener
    }

    fun setImageLoader(imageLoader: ImageLoader) {
        this.imageLoader = imageLoader
    }

    inner class SquareViewHolder(private val binding: ItemFolderSquareBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataSet[position]

            with(binding) {
                this.item = item
                item.thumbnail?.let { imageLoader.loadImage(it, thumbnail) }
                thumbnail.clipToOutline = true
                container.setOnClickListener {
                    listener.onItemClick(item)
                }
                more.setOnClickListener {
                    folderMoreDialogListener?.onItemMoreButtonClick(position, item)
                }
            }
        }
    }

    inner class HorizontalViewHolder(private val binding: ItemFolderHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataSet[position]
            with(binding) {
                this.item = item
                check.visibility = if (selectedFolderPosition == position) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
                thumbnail.clipToOutline = true
                item.thumbnail?.let { imageLoader.loadImage(it, thumbnail) }
                container.setOnClickListener {
                    selectedFolderPosition = position
                    listener.onItemClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (folderListViewType) {
            FolderListViewType.SQUARE_VIEW_TYPE -> {
                val binding = ItemFolderSquareBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
                return SquareViewHolder(binding)
            }

            FolderListViewType.HORIZONTAL_VIEW_TYPE -> {
                val binding = ItemFolderHorizontalBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
                return HorizontalViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is FolderListAdapter.SquareViewHolder -> viewHolder.bind(position)
            is FolderListAdapter.HorizontalViewHolder -> viewHolder.bind(position)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    override fun getItemId(position: Int): Long = position.toLong()

    fun addData(folderItem: FolderItem) {
        dataSet.add(0, folderItem)
        notifyItemInserted(0)
    }

    fun updateData(position: Int, folderItem: FolderItem) {
        dataSet[position] = folderItem
        notifyItemChanged(position)
    }

    fun deleteData(position: Int, folderItem: FolderItem) {
        dataSet.remove(folderItem)
        notifyItemRemoved(position)
    }

    fun setData(items: List<FolderItem>) {
        dataSet.clear()
        dataSet.addAll(items)
        notifyDataSetChanged()
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<FolderItem>() {
            override fun areItemsTheSame(
                oldItem: FolderItem,
                newItem: FolderItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: FolderItem,
                newItem: FolderItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}