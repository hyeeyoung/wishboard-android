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
import com.hyeeyoung.wishboard.databinding.DialogNewFolderAddBinding
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.viewmodel.FolderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderAddDialogFragment : DialogFragment() {
    private lateinit var binding: DialogNewFolderAddBinding
    private val viewModel: FolderViewModel by activityViewModels()
    private var folderItem: FolderItem? = null
    private var position: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNewFolderAddBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@FolderAddDialogFragment

        arguments?.let {
            (it[ARG_FOLDER_ITEM] as? FolderItem)?.let { folder ->
                viewModel.setEditMode(true)
                folderItem = folder
                viewModel.setFolderName(folder.name)
            }
            (it[ARG_FOLDER_POSITION] as? Int)?.let { position -> this.position = position }
        }

        addListeners()
        addObservers()

        return binding.root
    }

    private fun addListeners() {
        binding.add.setOnClickListener {
            when (viewModel.getEditMode().value) {
                true -> viewModel.updateFolderName(folderItem, position)
                false -> viewModel.createNewFolder()
            }
        }
        binding.close.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun addObservers() {
        viewModel.getIsCompleteUpload().observe(viewLifecycleOwner) { isComplete ->
            if (isComplete) {
                dismiss()
                val toastMessageRes = when (viewModel.getEditMode().value) {
                    true -> R.string.folder_name_update_toast_text
                    else -> R.string.folder_add_toast_text
                }
                Toast.makeText(requireContext(), getString(toastMessageRes), Toast.LENGTH_SHORT)
                    .show()

                viewModel.resetFolderData()
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
        private const val TAG = "FolderAdditionDialogFragment"
        private const val ARG_FOLDER_ITEM = "folderItem"
        private const val ARG_FOLDER_POSITION = "folderPosition"
    }
}