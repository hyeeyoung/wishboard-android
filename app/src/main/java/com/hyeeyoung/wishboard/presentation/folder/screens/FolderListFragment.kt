package com.hyeeyoung.wishboard.presentation.folder.screens

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.databinding.FragmentFolderListBinding
import com.hyeeyoung.wishboard.presentation.folder.FolderListAdapter
import com.hyeeyoung.wishboard.presentation.folder.types.FolderListViewType
import com.hyeeyoung.wishboard.presentation.wishitem.viewmodels.WishItemRegistrationViewModel
import com.hyeeyoung.wishboard.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderListFragment : BaseFragment<FragmentFolderListBinding>(R.layout.fragment_folder_list),
    FolderListAdapter.OnItemClickListener { // 현재 사용되지 않는 뷰이지만, 추후 사용될 가능성이 있기 때문에 삭제하지 않음
    private val viewModel: WishItemRegistrationViewModel by hiltNavGraphViewModels(R.id.wish_item_registration_nav_graph)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        initializeView()
    }

    private fun initializeView() {
        val adapter = FolderListAdapter(FolderListViewType.HORIZONTAL_VIEW_TYPE)
        adapter.setOnItemClickListener(this)
        binding.folderList.run {
            this.adapter = adapter
            itemAnimator = null
            setItemViewCacheSize(20)
        }
    }

    override fun onItemClick(item: FolderItem) {
        viewModel.setFolderItem(item)
        findNavController().popBackStack()
    }
}