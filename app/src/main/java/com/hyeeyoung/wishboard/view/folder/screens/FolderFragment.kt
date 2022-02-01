package com.hyeeyoung.wishboard.view.folder.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentFolderBinding
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.util.ImageLoader
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.loadImage
import com.hyeeyoung.wishboard.view.folder.adapters.FolderListAdapter
import com.hyeeyoung.wishboard.viewmodel.FolderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderFragment : Fragment(), FolderListAdapter.OnItemClickListener, ImageLoader {
    private lateinit var binding: FragmentFolderBinding
    private val viewModel: FolderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFolderBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewModel.fetchFolderList()

        initializeView()
        addListeners()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        val adapter = viewModel.getFolderListAdapter()
        adapter.setOnItemClickListener(this)
        adapter.setImageLoader(this)
        binding.folderList.adapter = adapter
        binding.folderList.layoutManager = GridLayoutManager(requireContext(), 2)

    }

    private fun addListeners() {
        binding.newFolder.setOnClickListener {
            findNavController().navigateSafe(R.id.action_folder_to_folder_add_dialog)
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchFolderList()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun addObservers() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<FolderItem>(
            ARG_FOLDER_ITEM
        )?.observe(viewLifecycleOwner) {
        }
    }

    override fun onItemClick(item: FolderItem) {
        findNavController().navigateSafe(
            R.id.action_folder_to_folder_detail,
            bundleOf(ARG_FOLDER_ITEM to item)
        )
    }

    override fun loadImage(imageUrl: String, imageView: ImageView) {
        loadImage(lifecycleScope, requireContext(), imageUrl, imageView)
    }

    companion object {
        private const val TAG = "FolderFragment"
        private const val ARG_FOLDER_ITEM = "folderItem"
    }
}