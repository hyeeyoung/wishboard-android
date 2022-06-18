package com.hyeeyoung.wishboard.presentation.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.databinding.ItemCartBinding
import com.hyeeyoung.wishboard.data.model.cart.CartItem
import com.hyeeyoung.wishboard.presentation.cart.types.CartItemButtonType
import com.hyeeyoung.wishboard.data.model.wish.WishItem

class CartListAdapter : ListAdapter<CartItem, RecyclerView.ViewHolder>(diffCallback) {
    private val dataSet = arrayListOf<CartItem>()
    private lateinit var listener: OnItemClickListener

    init {
        setHasStableIds(true)
    }

    interface OnItemClickListener {
        fun onItemClick(item: CartItem, position: Int, viewType: CartItemButtonType)
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
                itemImage.clipToOutline = true
                item.wishItem.imageUrl?.let { url ->
                    Glide.with(itemImage.context).load(url).into(itemImage)
                }
                delete.setOnClickListener {
                    listener.onItemClick(item, position, CartItemButtonType.VIEW_TYPE_DELETION)
                }
                itemImage.setOnClickListener {
                    listener.onItemClick(item, position, CartItemButtonType.VIEW_TYPE_CONTAINER)
                }
                itemName.setOnClickListener {
                    listener.onItemClick(item, position, CartItemButtonType.VIEW_TYPE_CONTAINER)
                }
                plus.setOnClickListener {
                    listener.onItemClick(item, position, CartItemButtonType.VIEW_TYPE_PLUS)
                }
                minus.setOnClickListener {
                    listener.onItemClick(item, position, CartItemButtonType.VIEW_TYPE_MINUS)
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

    override fun getItemId(position: Int): Long = dataSet[position].wishItem.id ?: 0

    fun getData(): List<CartItem> = dataSet

    fun setData(items: List<CartItem>) {
        dataSet.clear()
        dataSet.addAll(items)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }

    //TODO 해당 코드 확인 필요
    fun updateItem(position: Int, cartItem: CartItem) {
        dataSet[position] = cartItem
        notifyItemChanged(position)
    }

    fun updateItem(position: Int, wishItem: WishItem) {
        dataSet[position].wishItem = wishItem
        notifyItemChanged(position)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<CartItem>() {
            override fun areItemsTheSame(
                oldItem: CartItem,
                newItem: CartItem
            ): Boolean {
                return oldItem.wishItem.id == newItem.wishItem.id
            }

            override fun areContentsTheSame(
                oldItem: CartItem,
                newItem: CartItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}