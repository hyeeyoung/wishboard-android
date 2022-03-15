package com.hyeeyoung.wishboard.view.folder.screens

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogNewFolderAddBinding
import com.hyeeyoung.wishboard.model.common.ProcessStatus
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
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

        viewModel.resetFolderData()
        arguments.let {
            if (it == null) {
                viewModel.setEditMode(false)
                return@let
            }

            viewModel.setEditMode(true)
            (it[ARG_FOLDER_ITEM] as? FolderItem)?.let { folder ->
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
            dismiss()
        }
    }

    private fun addObservers() {
        viewModel.getIsCompleteUpload().observe(viewLifecycleOwner) { isComplete ->
            if (isComplete == true) {
                dismiss()
                val toastMessageRes = when (viewModel.getEditMode().value) {
                    true -> R.string.folder_name_update_snackbar_text
                    else -> R.string.folder_add_snackbar_text
                }
                CustomSnackbar.make(binding.layout, getString(toastMessageRes)).show()
            }
        }
        viewModel.getRegistrationStatus().observe(this) {
            when (it) {
                ProcessStatus.IDLE -> {
                    binding.loadingLottie.visibility = View.GONE
                }
                ProcessStatus.IN_PROGRESS -> {
                    binding.loadingLottie.visibility = View.VISIBLE
                    binding.loadingLottie.playAnimation()
                }
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
        const val TAG = "FolderAdditionDialogFragment"
        private const val ARG_FOLDER_ITEM = "folderItem"
        private const val ARG_FOLDER_POSITION = "folderPosition"
    }
}