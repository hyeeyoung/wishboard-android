package com.hyeeyoung.wishboard.presentation.home

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
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.presentation.wishitem.WishItemStatus
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), WishListAdapter.OnItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: WishListViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchWishList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@HomeFragment

        initializeView()
        addListeners()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        val adapter = viewModel.getWishListAdapter()
        adapter.setOnItemClickListener(this)
        binding.wishList.run {
            this.adapter = adapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            itemAnimator = null
            setItemViewCacheSize(20)
        }
    }

    private fun addListeners() {
        binding.cart.setOnClickListener {
            findNavController().navigateSafe(R.id.action_home_to_cart)
        }
        binding.calendar.setOnClickListener {
            findNavController().navigateSafe(R.id.action_home_to_calendar)
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchWishList()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun addObservers() {
        // 상세조회에서 아이템 삭제 완료 후 홈으로 복귀했을 때 해당 아이템 정보를 전달받고, ui를 업데이트
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(
            ARG_WISH_ITEM_INFO
        )?.observe(viewLifecycleOwner) {
            (it[ARG_ITEM_STATUS] as? WishItemStatus)?.let { status ->
                val position = it[ARG_WISH_ITEM_POSITION] as? Int
                val item = it[ARG_WISH_ITEM] as? WishItem
                when (status) {
                    WishItemStatus.MODIFIED -> {
                        viewModel.updateWishItem(position ?: return@let, item ?: return@let)
                    }
                    WishItemStatus.DELETED -> {
                        viewModel.deleteWishItem(position ?: return@let, item ?: return@let)
                    }
                    WishItemStatus.ADDED -> {
                        viewModel.fetchLatestItem()
                    }
                }
                // 단순 화면 전환 시에도 해당 코드 실행 방지를 위해 전달받은 bundle 데이터를 clear()
                it.clear()
                return@observe
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
        private const val ARG_ITEM_STATUS = "itemStatus"
    }
}