package com.hyeeyoung.wishboard.view.folder.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogFolderMoreBinding
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderMoreDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: DialogFolderMoreBinding
    private var folderItem: FolderItem? = null
    private var position: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFolderMoreBinding.inflate(inflater, container, false)

        arguments?.let {
            (it[ARG_FOLDER_ITEM] as? FolderItem)?.let { folderItem = it }
            (it[ARG_FOLDER_POSITION] as? Int)?.let { position = it }
        }

        addListener()

        return binding.root
    }

    private fun addListener() {
        binding.update.setOnClickListener {
            dismiss()
            TODO("not yet implemented")
        }
        binding.delete.setOnClickListener {
            dismiss()
            findNavController().navigateSafe(
                R.id.action_folder_more_to_folder_delete_dialog,
                bundleOf(
                    ARG_FOLDER_ITEM to folderItem,
                    ARG_FOLDER_POSITION to position
                )
            )
        }
    }

    companion object {
        private const val TAG = "FolderListFragment"
        private const val ARG_FOLDER_ITEM = "folderItem"
        private const val ARG_FOLDER_POSITION = "folderPosition"
    }
}