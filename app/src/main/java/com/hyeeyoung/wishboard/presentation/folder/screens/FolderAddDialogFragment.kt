package com.hyeeyoung.wishboard.presentation.folder.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.hyeeyoung.wishboard.databinding.DialogNewFolderAddBinding
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.util.DialogListener
import com.hyeeyoung.wishboard.presentation.folder.FolderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderAddDialogFragment : DialogFragment() {
    private lateinit var binding: DialogNewFolderAddBinding
    private val viewModel: FolderViewModel by activityViewModels()
    private lateinit var listener: DialogListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNewFolderAddBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        addListeners()
        addObservers()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
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

    companion object {
        const val TAG = "FolderAdditionDialogFragment"
    }
}