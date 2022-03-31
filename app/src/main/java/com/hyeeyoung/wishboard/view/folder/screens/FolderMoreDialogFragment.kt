package com.hyeeyoung.wishboard.view.folder.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyeeyoung.wishboard.databinding.DialogFolderMoreBinding
import com.hyeeyoung.wishboard.model.folder.FolderMoreDialogButtonReplyType
import com.hyeeyoung.wishboard.view.common.screens.DialogListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderMoreDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: DialogFolderMoreBinding
    private lateinit var listener: DialogListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFolderMoreBinding.inflate(inflater, container, false)

        addListeners()

        return binding.root
    }

    private fun addListeners() {
        binding.update.setOnClickListener {
            listener.onButtonClicked(FolderMoreDialogButtonReplyType.UPDATE.name)
        }
        binding.delete.setOnClickListener {
            listener.onButtonClicked(FolderMoreDialogButtonReplyType.DELETE.name)
        }
        binding.close.setOnClickListener {
            listener.onButtonClicked(FolderMoreDialogButtonReplyType.CLOSE.name)
        }
    }

    fun setListener(listener: DialogListener) {
        this.listener = listener
    }

    companion object {
        private const val TAG = "FolderMoreDialogFragment"
    }
}