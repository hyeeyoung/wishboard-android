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
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.view.cart.adapters.CartListAdapter
import com.hyeeyoung.wishboard.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment(), CartListAdapter.OnItemClickListener {
    private lateinit var binding: FragmentCartBinding
    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        initializeView()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        cartAdapter = CartListAdapter(requireContext())
        cartAdapter.setOnItemClickListener(this)
        binding.cartList.run {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
            binding.cartList.addItemDecoration(
                DividerItemDecoration(
                    binding.cartList.context,
                    LinearLayoutManager(requireContext()).orientation
                )
            )
        }
    }

    private fun addObservers() {
        viewModel.getCartList().observe(viewLifecycleOwner) {
            it?.let {
                cartAdapter.setData(it)
            }
        }
    }

    override fun onItemDeleteButtonClick(item: CartItem, isSelected: Boolean) {
        if (!isSelected) {
            viewModel.removeToCart(item.wishItem.id)
        }
    }

    override fun onItemClick(item: CartItem, viewType: String) {
        when (viewType) {
            VIEW_TYPE_CONTAINER -> {
                findNavController().navigateSafe(
                    R.id.action_home_to_wish_item_detail,
                    bundleOf(ARG_WISH_ITEM to item.wishItem)
                )
            }
        }
    }

    companion object {
        private const val TAG = "CartFragment"
        const val VIEW_TYPE_PLUS = "CartFragment"
        const val VIEW_TYPE_MINUS = "CartFragment"
        const val ARG_WISH_ITEM = "wishItem"
        const val VIEW_TYPE_CONTAINER = "CartFragment"
    }
}