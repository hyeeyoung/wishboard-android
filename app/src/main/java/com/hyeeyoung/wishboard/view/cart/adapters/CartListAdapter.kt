package com.hyeeyoung.wishboard.view.cart.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.databinding.ItemCartBinding
import com.hyeeyoung.wishboard.model.cart.CartItem
import com.hyeeyoung.wishboard.view.cart.screens.CartFragment.Companion.VIEW_TYPE_CONTAINER

class CartListAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dataSet = arrayListOf<CartItem>()
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemDeleteButtonClick(item: CartItem, isSelected: Boolean)
        fun onItemClick(item: CartItem, viewType: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataSet[position]

            with(binding) {
                this.item = item
                Glide.with(context).load(item.image).into(itemImage)
                delete.setOnClickListener {
                    listener.onItemDeleteButtonClick(item, it.isSelected)
                    removeItem(position)
                }
                container.setOnClickListener {
                    listener.onItemClick(item, VIEW_TYPE_CONTAINER)
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemCartBinding.inflate(
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

    fun setData(items: List<CartItem>) {
        dataSet.clear()
        dataSet.addAll(items)
        notifyDataSetChanged()
//        selectedCartButtons = Array(dataSet.size) { false }
    }

    fun removeItem(position: Int) {
        dataSet.removeAt(position)
        notifyItemChanged(position)
    }
}