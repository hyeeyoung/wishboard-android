package com.hyeeyoung.wishboard.view.wish.list.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentHomeBinding
import com.hyeeyoung.wishboard.model.WishItem
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.view.wish.item.screens.WishItemDetailFragment.Companion.ARG_ITEM_ID
import com.hyeeyoung.wishboard.view.wish.list.adapters.WishItemListAdapter
import com.hyeeyoung.wishboard.viewmodel.WishViewModel

class HomeFragment : Fragment(), WishItemListAdapter.OnItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: WishViewModel by activityViewModels()
   // private val cartViewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initializeView()
        addListeners()

        return binding.root
    }

    private fun initializeView() {
        val adapter = WishItemListAdapter(requireContext())
        adapter.setOnItemClickListener(this)
        adapter.setData(viewModel.getWishItems().value ?: return)
        binding.wishList.adapter = adapter
        binding.wishList.layoutManager = LinearLayoutManager(requireContext())

        // 주석 해제 후 테스트 해볼 수 있음
        // 로그인 한 계정으로 아이템 1개 등록 후 해당 아이템의 item_id값으로 변경 바람, 테스트 후 원상 복구 부탁
        findNavController().navigateSafe(
            R.id.action_home_to_wish_item_detail,
            bundleOf(ARG_ITEM_ID to 1)
        )
    }

    private fun addListeners() {
        binding.cart.setOnClickListener {
            findNavController().navigateSafe(R.id.action_home_to_cart)
        }
    }

        override fun onItemClick(item: WishItem) {
            findNavController().navigateSafe(
                R.id.action_home_to_wish_item_detail,
                bundleOf(ARG_ITEM_ID to item.itemId)
            )
        }

        override fun onCartBtnClick(itemId: Int, isAdded: Boolean) {
            // TODO not yet implemented
//        if (isAdded) {
//            cartViewModel.addToCart(item)
//        } else {
//            cartViewModel.removeToCart(item)
//        }
        }

        companion object {
            const val ARG_WISH_ITEM = "wishItem"
            private const val TAG = "HomeFragment"
        }
    }