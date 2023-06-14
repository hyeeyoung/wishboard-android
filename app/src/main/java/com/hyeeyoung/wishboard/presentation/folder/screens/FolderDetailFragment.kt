package com.hyeeyoung.wishboard.presentation.folder.screens

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.databinding.FragmentFolderDetailBinding
import com.hyeeyoung.wishboard.presentation.base.screen.NetworkFragment
import com.hyeeyoung.wishboard.presentation.home.WishListAdapter
import com.hyeeyoung.wishboard.presentation.home.WishListViewModel
import com.hyeeyoung.wishboard.presentation.wishitem.WishItemStatus
import com.hyeeyoung.wishboard.util.UiState
import com.hyeeyoung.wishboard.util.extension.collectFlow
import com.hyeeyoung.wishboard.util.extension.getParcelableValue
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.extension.safeValueOf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine

@AndroidEntryPoint
class FolderDetailFragment :
    NetworkFragment<FragmentFolderDetailBinding>(R.layout.fragment_folder_detail),
    WishListAdapter.OnItemClickListener {
    private val viewModel: WishListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val folder =
                it.getParcelableValue(ARG_FOLDER_ITEM, FolderItem::class.java) ?: return@let
            viewModel.setFolderItem(folder)
        }
    }

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
        val adapter = viewModel.getWishListAdapter().apply {
            setOnItemClickListener(this@FolderDetailFragment)
        }
        binding.wishList.adapter = adapter
    }

    private fun addListeners() {
        binding.topAppBar.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun addObservers() {
        // 상세조회에서 아이템 수정 및 삭제 후 폴더 디테일로 복귀했을 때 해당 아이템 정보를 전달받고, ui를 업데이트
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
                else -> {}
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
                viewModel.folderDetailListFetchState
            ) { isConnected, isSuccessful ->
                isConnected && isSuccessful !is UiState.Success
            }) { shouldFetch ->
            if (shouldFetch) viewModel.fetchFolderItems(viewModel.folderItem?.id)
        }
    }

    override fun onItemClick(position: Int, item: WishItem) {
        findNavController().navigateSafe(
            R.id.action_folder_detail_to_wish_item_detail,
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
        private const val ARG_WISH_ITEM_INFO = "wishItemInfo"
        private const val ARG_FOLDER_ITEM = "folderItem"
        private const val ARG_WISH_ITEM_POSITION = "position"
        private const val ARG_ITEM_STATUS = "itemStatus"
    }
}