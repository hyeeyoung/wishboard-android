package com.hyeeyoung.wishboard.presentation.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.data.model.cart.CartItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.databinding.FragmentCartBinding
import com.hyeeyoung.wishboard.presentation.cart.types.CartItemButtonType
import com.hyeeyoung.wishboard.presentation.wishitem.WishItemStatus
import com.hyeeyoung.wishboard.util.extension.getParcelableValue
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.extension.safeValueOf
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment(), CartListAdapter.OnItemClickListener {
    private lateinit var binding: FragmentCartBinding
    private val viewModel: CartViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initializeView()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        val adapter = viewModel.getCartListAdapter()
        adapter.setOnItemClickListener(this)

        binding.cartList.run {
            this.adapter = adapter
            itemAnimator = null
            setItemViewCacheSize(20)
        }
    }

    override fun onItemClick(item: CartItem, position: Int, viewType: CartItemButtonType) {
        when (viewType) {
            CartItemButtonType.VIEW_TYPE_CONTAINER -> {
                findNavController().navigateSafe(
                    R.id.action_home_to_wish_item_detail,
                    bundleOf(
                        ARG_WISH_ITEM_POSITION to position,
                        ARG_WISH_ITEM_ID to item.wishItem.id,
                    )
                )
            }
            CartItemButtonType.VIEW_TYPE_DELETION -> {
                viewModel.removeToCart(item.wishItem.id!!, position)
            }
            CartItemButtonType.VIEW_TYPE_PLUS, CartItemButtonType.VIEW_TYPE_MINUS -> {
                viewModel.controlItemCount(item, position, viewType)
            }
        }
    }

    private fun addObservers() {
        // 상세조회에서 아이템 수정 및 삭제 후 장바구니 복귀했을 때 해당 아이템 정보를 전달받고, ui를 업데이트
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(
            ARG_WISH_ITEM_INFO
        )?.observe(viewLifecycleOwner) { bundle ->
            val status =
                safeValueOf<WishItemStatus>(bundle.getString(ARG_ITEM_STATUS)) ?: return@observe
            val position = bundle.getInt(ARG_WISH_ITEM_POSITION)
            val item = bundle.getParcelableValue(ARG_WISH_ITEM_THUMBNAIL, WishItem::class.java)
            when (status) {
                WishItemStatus.MODIFIED -> {
                    viewModel.updateCartItem(position, item ?: return@observe)
                }
                WishItemStatus.DELETED -> {
                    viewModel.deleteCartItem(position)
                }
                else -> {}
            }

            // 단순 화면 전환 시에도 해당 코드 실행 방지를 위해 전달받은 bundle 데이터를 clear()
            bundle.clear()
            return@observe

        }
    }

    companion object {
        private const val ARG_WISH_ITEM_THUMBNAIL = "wishItemThumbnail"
        private const val ARG_WISH_ITEM_ID = "wishItemId"
        private const val ARG_WISH_ITEM_POSITION = "position"
        private const val ARG_WISH_ITEM_INFO = "wishItemInfo"
        private const val ARG_ITEM_STATUS = "itemStatus"
    }
}