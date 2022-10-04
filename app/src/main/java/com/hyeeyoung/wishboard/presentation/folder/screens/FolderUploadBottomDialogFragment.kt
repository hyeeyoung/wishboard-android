package com.hyeeyoung.wishboard.presentation.folder.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogBottomFolderUploadBinding
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.presentation.wishitem.viewmodels.WishItemRegistrationViewModel
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