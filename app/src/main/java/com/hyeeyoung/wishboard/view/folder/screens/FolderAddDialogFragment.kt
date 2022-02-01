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
import androidx.navigation.fragment.findNavController
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
            if (viewModel.getEditMode().value == true) {
                viewModel.updateFolderName(folderItem, position)
            } else {
                viewModel.createNewFolder()
            }
        }
        binding.close.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun addObservers() {
        viewModel.getIsCompleteAddition().observe(viewLifecycleOwner) {
            findNavController().apply {
                previousBackStackEntry?.savedStateHandle?.set(
                    ARG_FOLDER_ITEM,
                    viewModel.getFolderItem()
                )
                popBackStack()
            }
        }
        viewModel.getIsCompleteUpdate().observe(viewLifecycleOwner) { isComplete ->
            if (isComplete) {
                dismiss()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.folder_name_update_toast_text),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetCompleteUpdate()
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