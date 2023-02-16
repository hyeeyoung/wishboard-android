package com.hyeeyoung.wishboard.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.databinding.ItemWishBinding
import com.hyeeyoung.wishboard.presentation.cart.types.CartStateType
import com.hyeeyoung.wishboard.util.setOnSingleClickListener

class WishListAdapter : RecyclerView.Adapter<WishListAdapter.ViewHolder>() {
    private lateinit var inflater: LayoutInflater
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
                    binding.itemImage.load(url)
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

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): WishListAdapter.ViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(viewGroup.context)

        return ViewHolder(ItemWishBinding.inflate(inflater, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: WishListAdapter.ViewHolder, position: Int) {
        viewHolder.bind(position)
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
        dataSet[position] = wishItem.apply {
            // 상세뷰에서 아이템 수정 후 홈화면 썸네일 업데이트를 위해 WishItem을 전달
            if (wishItem.cartState == null) cartState = dataSet[position].cartState
        }
        notifyItemChanged(position)
    }

    fun deleteData(position: Int, wishItem: WishItem) {
        dataSet.remove(wishItem.apply {
            // 상세뷰에서 아이템 수정 후 홈화면 썸네일 업데이트를 위해 WishItem을 전달
            if (wishItem.cartState == null) cartState = dataSet[position].cartState
        })
        notifyItemRemoved(position)
    }

    fun setData(items: List<WishItem>?) {
        dataSet.clear()
        items?.let { dataSet.addAll(it) }
        notifyDataSetChanged()
    }
}