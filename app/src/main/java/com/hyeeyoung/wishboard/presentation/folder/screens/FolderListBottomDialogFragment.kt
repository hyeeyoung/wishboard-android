package com.hyeeyoung.wishboard.presentation.folder.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.databinding.DialogBottomFolderListBinding
import com.hyeeyoung.wishboard.presentation.base.screen.BaseBottomSheetDialogFragment
import com.hyeeyoung.wishboard.presentation.folder.FolderListAdapter
import com.hyeeyoung.wishboard.presentation.folder.FolderViewModel
import com.hyeeyoung.wishboard.presentation.folder.types.FolderListViewType
import com.hyeeyoung.wishboard.util.FolderListDialogListener
import com.hyeeyoung.wishboard.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderListBottomDialogFragment(private val folderId: Long?) :
    BaseBottomSheetDialogFragment<DialogBottomFolderListBinding>(
        R.layout.dialog_bottom_folder_list
    ), FolderListAdapter.OnItemClickListener {
    private val viewModel: FolderViewModel by viewModels()
    private lateinit var listener: FolderListDialogListener
    private val folderListAdapter =
        FolderListAdapter(FolderListViewType.HORIZONTAL_VIEW_TYPE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchFolderList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeView()
        addListeners()
        addObservers()
    }

    private fun initializeView() {
        val adapter = folderListAdapter
        folderId?.let { adapter.setSelectedFolder(it) }
        adapter.setOnItemClickListener(this@FolderListBottomDialogFragment)

        binding.folderList.run {
            this.adapter = adapter
            itemAnimator = null
            setItemViewCacheSize(20)
        }
    }

    private fun addListeners() {
        binding.topDialogBar.close.setOnClickListener {
            dismiss()
        }
    }

    private fun addObservers() {
        viewModel.folderFetchState.observe(viewLifecycleOwner) { fetchState ->
            when (fetchState) {
                is UiState.Success -> {
                    binding.noItemView.visibility = View.GONE
                    folderListAdapter.setData(items = fetchState.data)
                }
                else -> {}
            }
        }
    }

    fun setListener(listener: FolderListDialogListener) {
        this.listener = listener
    }

    override fun onItemClick(item: FolderItem) {
        listener.onButtonClicked(item)
    }
}