package com.hyeeyoung.wishboard.view.folder.screens

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogBottomFolderUploadBinding
import com.hyeeyoung.wishboard.model.common.ProcessStatus
import com.hyeeyoung.wishboard.viewmodel.WishItemRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderUploadBottomDialogFragment : DialogFragment() { // TODO rename
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

        addListeners()
        addObservers()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            resources.getDimensionPixelSize(R.dimen.widgetBottomDialogHeightBase)
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }

    private fun addListeners() {
        binding.add.setOnClickListener {
            // TODO 폴더 추가 요청
            dismiss()
        }
        binding.back.setOnClickListener {
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
            }
        }
    }

    companion object {
        const val TAG = "FolderUploadBottomDialogFragment"
    }
}