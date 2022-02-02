package com.hyeeyoung.wishboard.view.cart.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyeeyoung.wishboard.databinding.ItemCartBinding
import com.hyeeyoung.wishboard.model.cart.CartItem
import com.hyeeyoung.wishboard.model.cart.CartItemButtonType
import com.hyeeyoung.wishboard.util.ImageLoader

class CartListAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dataSet = arrayListOf<CartItem>()
    private lateinit var listener: OnItemClickListener
    private lateinit var imageLoader: ImageLoader

    interface OnItemClickListener {
        fun onItemClick(item: CartItem, position: Int, viewType: CartItemButtonType)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setImageLoader(imageLoader: ImageLoader) {
        this.imageLoader = imageLoader
    }

    inner class ViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataSet[position]

            with(binding) {
                this.item = item
                item.wishItem.image?.let { imageLoader.loadImage(it, binding.itemImage) }

                delete.setOnClickListener {
                    listener.onItemClick(item, position, CartItemButtonType.VIEW_TYPE_DELETION)
                }
                container.setOnClickListener {
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
}