package com.hyeeyoung.wishboard.presentation.cart

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.data.model.cart.CartItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.databinding.FragmentCartBinding
import com.hyeeyoung.wishboard.presentation.base.screen.NetworkFragment
import com.hyeeyoung.wishboard.presentation.cart.types.CartItemButtonType
import com.hyeeyoung.wishboard.presentation.common.screens.TwoButtonDialogFragment
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.presentation.wishitem.WishItemStatus
import com.hyeeyoung.wishboard.util.DialogListener
import com.hyeeyoung.wishboard.util.UiState
import com.hyeeyoung.wishboard.util.extension.collectFlow
import com.hyeeyoung.wishboard.util.extension.getParcelableValue
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.extension.safeValueOf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine

@AndroidEntryPoint
class CartFragment : NetworkFragment<FragmentCartBinding>(R.layout.fragment_cart),
    CartListAdapter.OnItemClickListener {
    private val viewModel: CartViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initializeView()
        addObservers()
        collectData()
    }

    private fun initializeView() {
        val adapter = viewModel.cartListAdapter
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
                    bundleOf(ARG_WISH_ITEM_ID to item.wishItem.id)
                )
            }
            CartItemButtonType.VIEW_TYPE_DELETION -> {
                showCartDeleteDialog(item)
            }
            CartItemButtonType.VIEW_TYPE_PLUS, CartItemButtonType.VIEW_TYPE_MINUS -> {
                viewModel.controlItemCount(item, viewType)
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
            val item = bundle.getParcelableValue(ARG_WISH_ITEM_THUMBNAIL, WishItem::class.java)
            when (status) {
                WishItemStatus.MODIFIED -> viewModel.updateCartItem(item ?: return@observe)
                WishItemStatus.DELETED -> viewModel.removeToCartList(item?.id ?: return@observe)
                else -> {}
            }

            // 단순 화면 전환 시에도 해당 코드 실행 방지를 위해 전달받은 bundle 데이터를 clear()
            bundle.clear()
            return@observe
        }
    }

    private fun collectData() {
        collectFlow(
            combine(isConnected, viewModel.cartFetchState) { isConnected, isSuccessful ->
                isConnected && isSuccessful !is UiState.Success
            }) { shouldFetch ->
            if (shouldFetch) viewModel.fetchCartList()
        }
    }

    private fun showCartDeleteDialog(cartItem: CartItem) {
        TwoButtonDialogFragment(
            getString(R.string.cart_delete_dialog_title),
            getString(R.string.cart_delete_dialog_description),
            getString(R.string.delete),
            getString(R.string.cancel)
        ).apply {
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: String) {
                    if (clicked == DialogButtonReplyType.YES.name)
                        viewModel.removeToCart(cartItem.wishItem.id ?: return)
                    dismiss()
                }
            })
        }.also {
            it.show(parentFragmentManager, "cartDeleteDialog")
        }
    }

    companion object {
        private const val ARG_WISH_ITEM_THUMBNAIL = "wishItemThumbnail"
        private const val ARG_WISH_ITEM_ID = "wishItemId"
        private const val ARG_WISH_ITEM_INFO = "wishItemInfo"
        private const val ARG_ITEM_STATUS = "itemStatus"
    }
}