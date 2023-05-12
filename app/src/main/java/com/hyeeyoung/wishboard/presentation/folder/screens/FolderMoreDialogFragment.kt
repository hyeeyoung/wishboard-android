package com.hyeeyoung.wishboard.presentation.folder.screens

import android.os.Bundle
import android.view.View
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogFolderMoreBinding
import com.hyeeyoung.wishboard.presentation.base.screen.BaseBottomSheetDialogFragment
import com.hyeeyoung.wishboard.presentation.folder.types.FolderMoreDialogButtonReplyType
import com.hyeeyoung.wishboard.util.DialogListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderMoreDialogFragment :
    BaseBottomSheetDialogFragment<DialogFolderMoreBinding>(R.layout.dialog_folder_more) {
    private lateinit var listener: DialogListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addListeners()
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
}