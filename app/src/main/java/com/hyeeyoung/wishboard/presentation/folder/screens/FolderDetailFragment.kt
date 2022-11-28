package com.hyeeyoung.wishboard.presentation.folder.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentFolderDetailBinding
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.presentation.wishitem.WishItemStatus
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.presentation.home.WishListAdapter
import com.hyeeyoung.wishboard.presentation.home.WishListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderDetailFragment : Fragment(), WishListAdapter.OnItemClickListener {
    private lateinit var binding: FragmentFolderDetailBinding
    private val viewModel: WishListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            (it[ARG_FOLDER_ITEM] as? FolderItem)?.let { folder ->
                viewModel.setFolderItem(folder)
                viewModel.fetchFolderItems(folder.id)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFolderDetailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initializeView()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        val adapter = viewModel.getWishListAdapter()
        adapter.setOnItemClickListener(this)
        binding.wishList.adapter = adapter
        binding.wishList.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun addObservers() {
        // 상세조회에서 아이템 수정 및 삭제 후 폴더 디테일로 복귀했을 때 해당 아이템 정보를 전달받고, ui를 업데이트
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(
            ARG_WISH_ITEM_INFO
        )?.observe(viewLifecycleOwner) {
            (it[ARG_ITEM_STATUS] as? WishItemStatus)?.let { status ->
                val position = it[ARG_WISH_ITEM_POSITION] as? Int
                val item = it[ARG_WISH_ITEM_THUMBNAIL] as? WishItem
                when (status) {
                    WishItemStatus.MODIFIED -> {
                        viewModel.updateWishItem(position ?: return@let, item ?: return@let)
                    }
                    WishItemStatus.DELETED -> {
                        viewModel.deleteWishItem(position ?: return@let, item ?: return@let)
                    }
                    else -> {}
                }
                // 단순 화면 전환 시에도 해당 코드 실행 방지를 위해 전달받은 bundle 데이터를 clear()
                it.clear()
                return@observe
            }
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