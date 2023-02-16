package com.hyeeyoung.wishboard.presentation.noti.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyeeyoung.wishboard.databinding.ItemCalendarNotiBinding
import com.hyeeyoung.wishboard.databinding.ItemNotiBinding
import com.hyeeyoung.wishboard.domain.model.NotiItemInfo
import com.hyeeyoung.wishboard.presentation.noti.types.NotiListViewType
import com.hyeeyoung.wishboard.presentation.noti.types.ReadStateType

class NotiListAdapter(
    private val notiListViewType: NotiListViewType,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dataSet = arrayListOf<NotiItemInfo>()
    private lateinit var listener: OnItemClickListener

    init {
        setHasStableIds(true)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: NotiItemInfo)
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

    fun setData(items: List<NotiItemInfo>?) {
        dataSet.clear()
        items?.let { dataSet.addAll(it) }
        notifyDataSetChanged()
    }
}