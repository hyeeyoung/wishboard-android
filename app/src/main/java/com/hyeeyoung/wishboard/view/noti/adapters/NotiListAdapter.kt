package com.hyeeyoung.wishboard.view.noti.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.databinding.ItemNotiBinding
import com.hyeeyoung.wishboard.model.noti.NotiItem
import com.hyeeyoung.wishboard.model.noti.ReadStateType

class NotiListAdapter : ListAdapter<NotiItem, RecyclerView.ViewHolder>(diffCallback) {
    private val dataSet = arrayListOf<NotiItem>()
    private lateinit var listener: OnItemClickListener

    init {
        setHasStableIds(true)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: NotiItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(private val binding: ItemNotiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataSet[position]
            with(binding) {
                this.item = item

                itemImage.clipToOutline = true
                item.itemImageUrl?.let {
                    Glide.with(itemImage.context).load(it).into(itemImage)
                }

                notiContainer.setOnClickListener {
                    listener.onItemClick(position, item)
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemNotiBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is ViewHolder -> viewHolder.bind(position)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    override fun getItemId(position: Int): Long = position.toLong()

    fun updateReadState(position: Int) {
        dataSet[position].readState = ReadStateType.READ.numValue
        notifyItemChanged(position)
    }

    fun setData(items: List<NotiItem>?) {
        dataSet.clear()
        items?.let { dataSet.addAll(it) }
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "notiListAdapter"
        private val diffCallback = object : DiffUtil.ItemCallback<NotiItem>() {
            override fun areItemsTheSame(
                oldItem: NotiItem,
                newItem: NotiItem
            ): Boolean {
                return oldItem.itemId == newItem.itemId
            }

            override fun areContentsTheSame(
                oldItem: NotiItem,
                newItem: NotiItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}