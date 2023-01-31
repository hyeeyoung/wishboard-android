package com.hyeeyoung.wishboard.presentation.folder.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyeeyoung.wishboard.databinding.DialogBottomFolderListBinding
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.presentation.folder.types.FolderListViewType
import com.hyeeyoung.wishboard.util.FolderListDialogListener
import com.hyeeyoung.wishboard.presentation.folder.FolderListAdapter
import com.hyeeyoung.wishboard.presentation.folder.FolderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderListBottomDialogFragment(private val folderId: Long?) : BottomSheetDialogFragment(), FolderListAdapter.OnItemClickListener {
    private lateinit var binding: DialogBottomFolderListBinding
    private val viewModel: FolderViewModel by viewModels()
    private lateinit var listener: FolderListDialogListener
    private val folderListAdapter =
        FolderListAdapter(FolderListViewType.HORIZONTAL_VIEW_TYPE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomFolderListBinding.inflate(inflater, container, false)

        initializeView()
        addListeners()
        addObservers()

        return binding.root
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
        binding.close.setOnClickListener {
            dismiss()
        }
    }

    private fun addObservers() {
        viewModel.getFolderList().observe(viewLifecycleOwner) {
            it?.let {
                if (it.isEmpty()) return@let
                binding.noItemView.visibility = View.GONE
                folderListAdapter.setData(it)
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