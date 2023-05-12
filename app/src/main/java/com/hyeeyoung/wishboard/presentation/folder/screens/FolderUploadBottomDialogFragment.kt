package com.hyeeyoung.wishboard.presentation.folder.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogBottomFolderUploadBinding
import com.hyeeyoung.wishboard.presentation.base.screen.BaseBottomSheetDialogFragment
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.presentation.wishitem.viewmodels.WishItemRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderUploadBottomDialogFragment :
    BaseBottomSheetDialogFragment<DialogBottomFolderUploadBinding>(R.layout.dialog_bottom_folder_upload) { // TODO rename
    private val viewModel: WishItemRegistrationViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // 키보드 위로 다이얼로그를 띄우기 위함
        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                sheet.parent.parent.requestLayout()
            }
        }

        viewModel.resetFolderName()
        viewModel.resetCompleteFolderUpload()

        addListeners()
        addObservers()
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
                else -> {}
            }
        }
        viewModel.isCompleteFolderUpload().observe(this) { isComplete ->
            if (isComplete == true) {
                dismiss()
            }
        }
    }
}