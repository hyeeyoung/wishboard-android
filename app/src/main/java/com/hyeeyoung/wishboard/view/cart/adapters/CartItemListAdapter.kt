package com.hyeeyoung.wishboard.view.cart.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.databinding.ItemCartBinding
import com.hyeeyoung.wishboard.model.cart.CartItem

class CartItemListAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dataSet = arrayListOf<CartItem>()
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(item: CartItem)
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
                container.setOnClickListener {
                    listener.onItemClick(item)
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
//        selectedCartButtons = Array(dataSet.size) { false }
    }
}