package com.hyeeyoung.wishboard.presentation.noti

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
    private lateinit var inflater: LayoutInflater
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

    class NotiTabViewHolder(private val binding: ItemNotiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, item: NotiItemInfo, listener: OnItemClickListener) {
            with(binding) {
                this.item = item
                notiContainer.setOnClickListener {
                    listener.onItemClick(position, item)
                }
            }
        }
    }

    class CalendarViewHolder(private val binding: ItemCalendarNotiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, item: NotiItemInfo, listener: OnItemClickListener) {
            with(binding) {
                this.item = item
                notiContainer.setOnClickListener {
                    listener.onItemClick(position, item)
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(viewGroup.context)

        return when (notiListViewType) {
            NotiListViewType.NOTI_TAB_VIEW_TYPE ->
                NotiTabViewHolder(ItemNotiBinding.inflate(inflater, viewGroup, false))
            NotiListViewType.CALENDAR_VIEW_TYPE ->
                CalendarViewHolder(ItemCalendarNotiBinding.inflate(inflater, viewGroup, false))
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is NotiTabViewHolder -> viewHolder.bind(position, dataSet[position], listener)
            is CalendarViewHolder -> viewHolder.bind(position, dataSet[position], listener)
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