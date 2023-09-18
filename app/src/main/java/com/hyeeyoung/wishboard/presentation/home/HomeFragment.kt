package com.hyeeyoung.wishboard.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.databinding.FragmentHomeBinding
import com.hyeeyoung.wishboard.presentation.base.screen.NetworkFragment
import com.hyeeyoung.wishboard.presentation.calendar.screen.CalendarActivity
import com.hyeeyoung.wishboard.presentation.common.screens.WebViewActivity
import com.hyeeyoung.wishboard.presentation.common.screens.WebViewActivity.Companion.ARG_WEB_VIEW_LINK
import com.hyeeyoung.wishboard.presentation.wishitem.WishItemStatus
import com.hyeeyoung.wishboard.util.UiState
import com.hyeeyoung.wishboard.util.extension.collectFlow
import com.hyeeyoung.wishboard.util.extension.getParcelableValue
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.extension.safeValueOf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine

@AndroidEntryPoint
class HomeFragment : NetworkFragment<FragmentHomeBinding>(R.layout.fragment_home),
    WishListAdapter.OnItemClickListener {
    private val viewModel: WishListViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initializeView()
        addListeners()
        addObservers()
        collectData()
    }

    private fun initializeView() {
        val adapter = viewModel.getWishListAdapter()
        adapter.setOnItemClickListener(this)
        binding.wishList.run {
            this.adapter = adapter
            itemAnimator = null
            setItemViewCacheSize(20)
        }
    }

    private fun addListeners() {
        binding.eventView.setOnClickListener {
            val intent = Intent(requireContext(), WebViewActivity::class.java)
            intent.putExtra(
                ARG_WEB_VIEW_LINK,
                "https://docs.google.com/forms/d/e/1FAIpQLSenh6xOvlDa61iw1UKBSM6SixdrgF17_i91Brb2osZcxB7MOQ/viewform"
            )
            startActivity(intent)
        }
        binding.eventClose.setOnClickListener {
            viewModel.updateEventXBtnClickTime()
        }
        binding.cart.setOnClickListener {
            findNavController().navigateSafe(R.id.action_home_to_cart)
        }
        binding.calendar.setOnClickListener {
            startActivity(Intent(requireContext(), CalendarActivity::class.java))
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
        )?.observe(viewLifecycleOwner) { bundle ->
            val status =
                safeValueOf<WishItemStatus>(bundle.getString(ARG_ITEM_STATUS)) ?: return@observe
            val position = bundle.getInt(ARG_WISH_ITEM_POSITION)
            val item = bundle.getParcelableValue(ARG_WISH_ITEM_THUMBNAIL, WishItem::class.java)
            when (status) {
                WishItemStatus.MODIFIED -> {
                    viewModel.updateWishItem(position, item ?: return@observe)
                }

                WishItemStatus.DELETED -> {
                    viewModel.deleteWishItem(position, item ?: return@observe)
                }

                WishItemStatus.ADDED -> {
                    viewModel.fetchLatestItem()
                }
            }
            // 단순 화면 전환 시에도 해당 코드 실행 방지를 위해 전달받은 bundle 데이터를 clear()
            bundle.clear()
            return@observe
        }
    }

    private fun collectData() {
        collectFlow(
            combine(
                isConnected,
                viewModel.wishListFetchState
            ) { isConnected, isSuccessful ->
                isConnected && isSuccessful !is UiState.Success
            }) { shouldFetch ->
            if (shouldFetch) viewModel.fetchWishList()
        }
    }

    override fun onItemClick(position: Int, item: WishItem) {
        findNavController().navigateSafe(
            R.id.action_home_to_wish_item_detail,
            bundleOf(
                ARG_WISH_ITEM_POSITION to position,
                ARG_WISH_ITEM_ID to item.id,
            )
        )
    }

    override fun onCartBtnClick(position: Int, item: WishItem) {
        viewModel.toggleCartState(position, item)
    }

    companion object {
        private const val ARG_WISH_ITEM_THUMBNAIL = "wishItemThumbnail"
        private const val ARG_WISH_ITEM_ID = "wishItemId"
        private const val ARG_WISH_ITEM_POSITION = "position"
        private const val ARG_WISH_ITEM_INFO = "wishItemInfo"
        private const val ARG_ITEM_STATUS = "itemStatus"
    }
}