package com.hyeeyoung.wishboard.presentation.noti.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.databinding.ItemCalendarNotiBinding
import com.hyeeyoung.wishboard.databinding.ItemNotiBinding
import com.hyeeyoung.wishboard.data.model.noti.NotiItem
import com.hyeeyoung.wishboard.presentation.noti.types.NotiListViewType
import com.hyeeyoung.wishboard.presentation.noti.types.ReadStateType

class NotiListAdapter(
    private val notiListViewType: NotiListViewType,
) : ListAdapter<NotiItem, RecyclerView.ViewHolder>(diffCallback) {
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

    inner class NotiTabViewHolder(private val binding: ItemNotiBinding) :
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

    inner class CalendarViewHolder(private val binding: ItemCalendarNotiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) { // TODO need refactoring
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
        when (notiListViewType) {
            NotiListViewType.NOTI_TAB_VIEW_TYPE -> {
                val binding = ItemNotiBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
                return NotiTabViewHolder(binding)
            }
            NotiListViewType.CALENDAR_VIEW_TYPE -> {
                val binding = ItemCalendarNotiBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
                return CalendarViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is NotiTabViewHolder -> viewHolder.bind(position)
            is CalendarViewHolder -> viewHolder.bind(position)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    override fun getItemId(position: Int): Long = dataSet[position].itemId

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