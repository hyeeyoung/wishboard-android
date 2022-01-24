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
import com.hyeeyoung.wishboard.util.safeLet
import com.hyeeyoung.wishboard.view.wish.list.adapters.WishListAdapter
import com.hyeeyoung.wishboard.viewmodel.WishListViewModel

class HomeFragment : Fragment(), WishListAdapter.OnItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: WishListViewModel by activityViewModels()
    private lateinit var adapter: WishListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initializeView()
        addListeners()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        adapter = viewModel.getWishListAdapter()
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

    // TODO addObservers() 삭제 -> viewModel에서 adapter.setData() 호출
    private fun addObservers() {
        viewModel.getWishList().observe(viewLifecycleOwner) {
            it?.let {
                adapter.setData(it)
            }
        }

        // 상세조회에서 아이템 삭제 완료 후 홈으로 복귀했을 때 해당 아이템 정보를 전달받고, ui를 업데이트
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(
            ARG_WISH_ITEM_INFO
        )?.observe(viewLifecycleOwner) {
            safeLet(
                it[ARG_WISH_ITEM_POSITION] as? Int,
                it[ARG_WISH_ITEM] as? WishItem
            ) { position, withITem ->
                viewModel.deleteWishItem(position, withITem)
            }
        }
    }

    override fun onItemClick(position: Int, item: WishItem) {
        findNavController().navigateSafe(
            R.id.action_home_to_wish_item_detail,
            bundleOf(
                ARG_WISH_ITEM_POSITION to position,
                ARG_WISH_ITEM to item,
            )
        )
    }

    override fun onCartBtnClick(position: Int, item: WishItem) {
        viewModel.toggleCartState(position, item)
    }

    companion object {
        private const val TAG = "HomeFragment"
        private const val ARG_WISH_ITEM = "wishItem"
        private const val ARG_WISH_ITEM_POSITION = "position"
        private const val ARG_WISH_ITEM_INFO = "wishItemInfo"
    }
}