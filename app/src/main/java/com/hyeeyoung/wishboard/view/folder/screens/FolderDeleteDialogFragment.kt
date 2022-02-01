package com.hyeeyoung.wishboard.view.folder.screens

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogFolderDeleteBinding
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.viewmodel.FolderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderDeleteDialogFragment : DialogFragment() {
    private lateinit var binding: DialogFolderDeleteBinding
    private val viewModel: FolderViewModel by activityViewModels()
    private var folderItem: FolderItem? = null
    private var position: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFolderDeleteBinding.inflate(inflater, container, false)

        arguments?.let {
            (it[ARG_FOLDER_ITEM] as? FolderItem)?.let { folder -> folderItem = folder }
            (it[ARG_FOLDER_POSITION] as? Int)?.let { position -> this.position = position }
        }

        addListeners()
        addObservers()

        return binding.root
    }

    private fun addListeners() {
        binding.yes.setOnClickListener {
            viewModel.deleteFolder(folderItem, position)
        }
        binding.no.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun addObservers() {
        viewModel.getIsCompleteDeletion().observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                dismiss()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.folder_delete_toast_text),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetCompleteDeletion()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    companion object {
        private const val TAG = "FolderDeleteDialogFragment"
        private const val ARG_FOLDER_ITEM = "folderItem"
        private const val ARG_FOLDER_POSITION = "folderPosition"
    }
}