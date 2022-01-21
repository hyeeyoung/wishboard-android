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
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(item: WishItem)
        fun onCartBtnClick(position: Int, item: WishItem, isSelected: Boolean)
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
                binding.cart.isSelected = item.cartId != null

                container.setOnClickListener {
                    listener.onItemClick(item)
                }

                cart.setOnClickListener {
                    listener.onCartBtnClick(position, item, binding.cart.isSelected)
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

    /** 아이템 정보(타이틀 및 가격 등)수정, cart 포함 여부 수정에 사용 */
    fun updateData(position: Int, wishItem: WishItem) {
        dataSet[position] = wishItem
        notifyItemChanged(position)
    }

    fun setData(items: List<WishItem>) {
        dataSet.clear()
        dataSet.addAll(items)
        notifyDataSetChanged()
    }
}