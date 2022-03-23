package com.hyeeyoung.wishboard.view.folder.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyeeyoung.wishboard.databinding.DialogBottomFolderUploadBinding
import com.hyeeyoung.wishboard.model.common.ProcessStatus
import com.hyeeyoung.wishboard.viewmodel.WishItemRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderUploadBottomDialogFragment() : BottomSheetDialogFragment() { // TODO rename
    private lateinit var binding: DialogBottomFolderUploadBinding
    private val viewModel: WishItemRegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomFolderUploadBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@FolderUploadBottomDialogFragment

        viewModel.resetFolderName()
        viewModel.resetCompleteFolderUpload()

        addListeners()
        addObservers()

        return binding.root
    }

    private fun addListeners() {
        binding.add.setOnClickListener {
            viewModel.createNewFolder()
        }
        binding.back.setOnClickListener {
            dismiss()
        }
    }

    private fun addObservers() {
        viewModel.getFolderRegistrationStatus().observe(this) {
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
        viewModel.isCompleteFolderUpload().observe(this) { isComplete ->
            if (isComplete == true) {
                dismiss()
            }
        }
    }

    companion object {
        const val TAG = "FolderUploadBottomDialogFragment"
    }
}