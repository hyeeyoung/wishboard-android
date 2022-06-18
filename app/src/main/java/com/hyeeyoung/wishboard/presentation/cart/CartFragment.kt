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
import com.hyeeyoung.wishboard.databinding.FragmentCartBinding
import com.hyeeyoung.wishboard.data.model.cart.CartItem
import com.hyeeyoung.wishboard.presentation.cart.types.CartItemButtonType
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.presentation.wishitem.WishItemStatus
import com.hyeeyoung.wishboard.util.extension.navigateSafe
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
        binding.lifecycleOwner = this@CartFragment

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
                        ARG_WISH_ITEM to item.wishItem
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
        )?.observe(viewLifecycleOwner) {
            (it[ARG_ITEM_STATUS] as? WishItemStatus)?.let { status ->
                val position = it[ARG_WISH_ITEM_POSITION] as? Int
                val item = it[ARG_WISH_ITEM] as? WishItem
                when (status) {
                    WishItemStatus.MODIFIED -> {
                        viewModel.updateCartItem(position ?: return@let, item ?: return@let)
                    }
                    WishItemStatus.DELETED -> {
                        viewModel.deleteCartItem(position ?: return@let)
                    }
                }
                // 단순 화면 전환 시에도 해당 코드 실행 방지를 위해 전달받은 bundle 데이터를 clear()
                it.clear()
                return@observe
            }
        }
    }

    companion object {
        private const val TAG = "CartFragment"
        const val ARG_WISH_ITEM = "wishItem"
        private const val ARG_WISH_ITEM_POSITION = "position"
        private const val ARG_WISH_ITEM_INFO = "wishItemInfo"
        private const val ARG_ITEM_STATUS = "itemStatus"
    }
}