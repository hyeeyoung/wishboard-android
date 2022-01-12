package com.hyeeyoung.wishboard.view.wish.list.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentHomeBinding
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.view.wish.list.adapters.WishListAdapter
import com.hyeeyoung.wishboard.viewmodel.WishViewModel

class HomeFragment : Fragment(), WishListAdapter.OnItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: WishViewModel by activityViewModels()
    private lateinit var adapter: WishListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initializeView()
        addListeners()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        adapter = WishListAdapter(requireContext())
        adapter.setOnItemClickListener(this)
        binding.wishList.adapter = adapter
        binding.wishList.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun addListeners() {
        binding.cart.setOnClickListener {
            findNavController().navigateSafe(R.id.action_home_to_cart)
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchWishList()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun addObservers() {
        viewModel.getWishList().observe(viewLifecycleOwner) {
            it?.let {
                adapter.setData(it)
            }
        }
    }

    override fun onItemClick(item: WishItem) {
        findNavController().navigateSafe(
            R.id.action_home_to_wish_item_detail,
            bundleOf(ARG_WISH_ITEM to item)
        )
    }

    override fun onCartBtnClick(itemId: Long, isSelected: Boolean) {
        if (isSelected) {
            viewModel.addToCart(itemId)
        } else {
            viewModel.removeToCart(itemId)
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
        const val ARG_WISH_ITEM = "wishItem"
    }
}