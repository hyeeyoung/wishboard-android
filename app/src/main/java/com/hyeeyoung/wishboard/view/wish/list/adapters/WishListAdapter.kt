package com.hyeeyoung.wishboard.view.wish.list.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.databinding.ItemWishBinding
import com.hyeeyoung.wishboard.model.wish.WishItem

class WishListAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dataSet = arrayListOf<WishItem>()
    private lateinit var selectedCartButtons: Array<Boolean>
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(item: WishItem)
        fun onCartBtnClick(itemId: Long, isAdded: Boolean)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(private val binding: ItemWishBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataSet[position]

            with(binding) {
                this.item = item
                Glide.with(context).load(item.image).into(itemImage)

                if (item.cartId == null) {
                    binding.cart.isSelected = false
                    selectedCartButtons[position] = false
                } else {
                    binding.cart.isSelected = true
                    selectedCartButtons[position] = true
                }

                container.setOnClickListener {
                    listener.onItemClick(item)
                }

                cart.setOnClickListener {
                    it.isSelected = !it.isSelected
                    selectedCartButtons[position] = !selectedCartButtons[position]
                    listener.onCartBtnClick(item.itemId, selectedCartButtons[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ItemWishBinding.inflate(
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

    fun setData(items: List<WishItem>) {
        dataSet.clear()
        dataSet.addAll(items)
        selectedCartButtons = Array(dataSet.size) { false }
        notifyDataSetChanged()
    }
}