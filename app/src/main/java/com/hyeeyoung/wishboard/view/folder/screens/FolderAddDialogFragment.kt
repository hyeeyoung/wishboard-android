package com.hyeeyoung.wishboard.view.folder.screens

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.databinding.DialogNewFolderAddBinding
import com.hyeeyoung.wishboard.viewmodel.FolderAddDialogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderAddDialogFragment : DialogFragment() {
    private lateinit var binding: DialogNewFolderAddBinding
    private val viewModel: FolderAddDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNewFolderAddBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@FolderAddDialogFragment

        addListeners()
        addObservers()

        return binding.root
    }

    private fun addListeners() {
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
    }
}