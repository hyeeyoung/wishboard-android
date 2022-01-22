package com.hyeeyoung.wishboard.view.cart.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentCartBinding
import com.hyeeyoung.wishboard.model.cart.CartItem
import com.hyeeyoung.wishboard.model.cart.CartItemButtonType
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.view.cart.adapters.CartListAdapter
import com.hyeeyoung.wishboard.viewmodel.CartViewModel
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

        return binding.root
    }

    private fun initializeView() {
        val adapter = viewModel.getCartListAdapter()
        adapter.setOnItemClickListener(this)
        binding.cartList.run {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
            binding.cartList.addItemDecoration(
                DividerItemDecoration(
                    binding.cartList.context,
                    LinearLayoutManager(requireContext()).orientation
                )
            )
        }
    }

    override fun onItemClick(item: CartItem, position: Int, viewType: CartItemButtonType) {
        when (viewType) {
            CartItemButtonType.VIEW_TYPE_CONTAINER -> {
                findNavController().navigateSafe(
                    R.id.action_home_to_wish_item_detail,
                    bundleOf(ARG_WISH_ITEM to item.wishItem)
                )
            }
            CartItemButtonType.VIEW_TYPE_DELETION -> {
                viewModel.removeToCart(item.wishItem.id!!)
            }
            CartItemButtonType.VIEW_TYPE_PLUS, CartItemButtonType.VIEW_TYPE_MINUS -> {
                viewModel.controlItemCount(item, position, viewType)
            }
        }
    }

    companion object {
        private const val TAG = "CartFragment"
        const val ARG_WISH_ITEM = "wishItem"
    }
}