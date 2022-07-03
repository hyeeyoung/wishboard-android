package com.hyeeyoung.wishboard.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.databinding.ItemWishBinding
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.presentation.cart.types.CartStateType
import com.hyeeyoung.wishboard.util.setOnSingleClickListener

class WishListAdapter : ListAdapter<WishItem, RecyclerView.ViewHolder>(diffCallback) {
    private val dataSet = arrayListOf<WishItem>()
    private lateinit var listener: OnItemClickListener

    init {
        setHasStableIds(true)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: WishItem)
        fun onCartBtnClick(position: Int, item: WishItem)
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

                item.imageUrl?.let { url ->
                    Glide.with(itemImage.context).load(url).into(itemImage)
                }

                cart.isSelected = item.cartState == CartStateType.IN_CART.numValue

                container.setOnClickListener {
                    listener.onItemClick(position, item)
                }

                cart.setOnSingleClickListener {
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

    fun getData(): List<WishItem> = dataSet

    override fun getItemCount(): Int = dataSet.size

    override fun getItemId(position: Int): Long = dataSet[position].id ?: 0

    fun insertData(wishItem: WishItem) {
        dataSet.add(0, wishItem)
        notifyItemInserted(0)
    }

    /** 아이템 정보(타이틀 및 가격 등)수정, cart 포함 여부 수정에 사용 */
    fun updateData(position: Int, wishItem: WishItem) {
        dataSet[position] = wishItem
        notifyItemChanged(position)
    }

    fun deleteData(position: Int, wishItem: WishItem) {
        dataSet.remove(wishItem)
        notifyItemRemoved(position)
    }

    fun setData(items: List<WishItem>?) {
        dataSet.clear()
        items?.let { dataSet.addAll(it) }
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "wishListAdapter"
        private val diffCallback = object : DiffUtil.ItemCallback<WishItem>() {
            override fun areItemsTheSame(
                oldItem: WishItem,
                newItem: WishItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: WishItem,
                newItem: WishItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}