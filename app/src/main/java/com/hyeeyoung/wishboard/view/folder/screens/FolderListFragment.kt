package com.hyeeyoung.wishboard.view.folder.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyeeyoung.wishboard.databinding.FragmentFolderListBinding
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.view.folder.adapters.FolderListAdapter
import com.hyeeyoung.wishboard.viewmodel.FolderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderListFragment : Fragment(), FolderListAdapter.OnItemClickListener {
    private lateinit var binding: FragmentFolderListBinding
    private val viewModel: FolderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFolderListBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewModel.fetchFolderListSummary()

        initializeView()

        return binding.root
    }

    private fun initializeView() {
        val adapter = viewModel.getFolderListSummaryAdapter()
        adapter.setOnItemClickListener(this)
        binding.folderList.adapter = adapter
        binding.folderList.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onItemClick(item: FolderItem) {
        findNavController().apply {
            previousBackStackEntry?.savedStateHandle?.set(ARG_FOLDER_ITEM, item)
            popBackStack()
        }
    }

    companion object {
        private const val TAG = "FolderListFragment"
        private const val ARG_FOLDER_ITEM = "folderItem"
    }
}