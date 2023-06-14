package com.hyeeyoung.wishboard.presentation.folder.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogNewFolderAddBinding
import com.hyeeyoung.wishboard.presentation.base.screen.BaseDialogFragment
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.presentation.folder.FolderViewModel
import com.hyeeyoung.wishboard.util.DialogListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderAddDialogFragment :
    BaseDialogFragment<DialogNewFolderAddBinding>(R.layout.dialog_new_folder_add) {
    // 현재 사용하지 않는 화면이지만 추후 사용될 가능성이 있으므로 삭제하지 않음
    private val viewModel: FolderViewModel by activityViewModels()
    private lateinit var listener: DialogListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        addListeners()
        addObservers()
    }

    private fun addListeners() {
        binding.add.setOnClickListener {
            listener.onButtonClicked(DialogButtonReplyType.YES.name)
        }
        binding.close.setOnClickListener {
            listener.onButtonClicked(DialogButtonReplyType.CLOSE.name)
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
    }

    fun setListener(listener: DialogListener) {
        this.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetFolderData()
    }
}