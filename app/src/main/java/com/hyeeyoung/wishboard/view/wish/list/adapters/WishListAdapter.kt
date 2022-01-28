package com.hyeeyoung.wishboard.view.wish.list.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyeeyoung.wishboard.databinding.ItemWishBinding
import com.hyeeyoung.wishboard.model.cart.CartStateType
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.util.ImageLoader

class WishListAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dataSet = arrayListOf<WishItem>()
    private lateinit var listener: OnItemClickListener
    private lateinit var imageLoader: ImageLoader

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: WishItem)
        fun onCartBtnClick(position: Int, item: WishItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setImageLoader(imageLoader: ImageLoader) {
        this.imageLoader = imageLoader
    }

    inner class ViewHolder(private val binding: ItemWishBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = dataSet[position]
            with(binding) {
                this.item = item
                imageLoader.loadImage(item.image ?: return@with, binding.itemImage)
                binding.cart.isSelected = item.cartState == CartStateType.IN_CART.numValue

                container.setOnClickListener {
                    listener.onItemClick(position, item)
                }

                cart.setOnClickListener {
                    listener.onCartBtnClick(position, item)
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

    fun deleteData(position: Int, wishItem: WishItem) {
        dataSet.remove(wishItem)
        notifyItemRemoved(position)
    }

    fun setData(items: List<WishItem>) {
        dataSet.clear()
        dataSet.addAll(items)
        notifyDataSetChanged()
    }
}