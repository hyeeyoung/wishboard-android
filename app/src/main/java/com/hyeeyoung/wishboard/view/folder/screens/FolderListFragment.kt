package com.hyeeyoung.wishboard.view.folder.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentFolderListBinding
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.view.folder.adapters.FolderListAdapter
import com.hyeeyoung.wishboard.viewmodel.WishItemRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderListFragment : Fragment(), FolderListAdapter.OnItemClickListener {
    private lateinit var binding: FragmentFolderListBinding
    private val viewModel: WishItemRegistrationViewModel by hiltNavGraphViewModels(R.id.wish_item_registration_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFolderListBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        initializeView()

        return binding.root
    }

    private fun initializeView() {
        val adapter = viewModel.getFolderListAdapter()
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

    companion object {
        private const val TAG = "FolderListFragment"
    }
}