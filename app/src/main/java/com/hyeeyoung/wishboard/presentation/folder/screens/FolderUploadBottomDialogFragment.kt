package com.hyeeyoung.wishboard.presentation.folder.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.databinding.DialogBottomFolderUploadBinding
import com.hyeeyoung.wishboard.presentation.base.screen.BaseBottomSheetDialogFragment
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.presentation.folder.FolderViewModel
import com.hyeeyoung.wishboard.presentation.folder.screens.FolderFragment.Companion.ARG_FOLDER_ITEM
import com.hyeeyoung.wishboard.util.UiState
import com.hyeeyoung.wishboard.util.extension.getParcelableValue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderUploadBottomDialogFragment :
    BaseBottomSheetDialogFragment<DialogBottomFolderUploadBinding>(R.layout.dialog_bottom_folder_upload) { // TODO rename
    private val viewModel: FolderViewModel by viewModels()
    private lateinit var listener: OnFolderUploadListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments.let {
            val folder = it?.getParcelableValue(ARG_FOLDER_ITEM, FolderItem::class.java)
            viewModel.setEditMode(folder != null)
            viewModel.setFolderInfo(folder ?: return@let)
        }
    }

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

        addListeners()
        addObservers()
    }

    private fun addListeners() {
        binding.topDialogBar.close.setOnClickListener {
            dismiss()
        }
    }

    private fun addObservers() {
        viewModel.getRegistrationStatus().observe(this) {
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

        viewModel.folderAddState.observe(viewLifecycleOwner) { addState ->
            when (addState) {
                is UiState.Success -> {
                    dismiss()
                    listener.onSuccessUpload(addState.data ?: return@observe)
                }
                else -> {
                    listener.onFailureUpload()
                }
            }
        }

        viewModel.folderUpdateState.observe(viewLifecycleOwner) { updateState ->
            when (updateState) {
                is UiState.Success -> {
                    dismiss()
                    listener.onSuccessUpload(updateState.data.second, updateState.data.first)
                }
                else -> {
                    listener.onFailureUpload()
                }
            }
        }
    }

    fun setListener(listener: OnFolderUploadListener) {
        this.listener = listener
    }

    interface OnFolderUploadListener {
        fun onSuccessUpload(newFolder: FolderItem, oldFolder: FolderItem? = null)
        fun onFailureUpload()
    }

    companion object {
        /** 폴더 수정 필요 시 프래그먼트 객체 생성 시 FolderItem 전달 필요 */
        fun newInstance(folder: FolderItem?): FolderUploadBottomDialogFragment {
            val args = Bundle().apply {
                putParcelable(ARG_FOLDER_ITEM, folder)
            }

            return FolderUploadBottomDialogFragment().apply { arguments = args }
        }
    }
}