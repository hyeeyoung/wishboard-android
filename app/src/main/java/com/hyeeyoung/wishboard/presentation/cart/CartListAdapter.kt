package com.hyeeyoung.wishboard.presentation.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyeeyoung.wishboard.data.model.cart.CartItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.databinding.ItemCartBinding
import com.hyeeyoung.wishboard.presentation.cart.types.CartItemButtonType

class CartListAdapter : RecyclerView.Adapter<CartListAdapter.ViewHolder>() {
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

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): CartListAdapter.ViewHolder {
        return ViewHolder(
            ItemCartBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: CartListAdapter.ViewHolder, position: Int) {
        viewHolder.bind(position)
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
        dataSet[position].wishItem = wishItem.apply {
            // 상세뷰에서 아이템 수정 후 홈화면 썸네일 업데이트를 위해 WishItem을 전달
            if (wishItem.cartState == null) cartState = dataSet[position].wishItem.cartState
        }
        notifyItemChanged(position)
    }
}