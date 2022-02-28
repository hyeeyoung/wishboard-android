package com.hyeeyoung.wishboard.view.folder.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogFolderMoreBinding
import com.hyeeyoung.wishboard.model.common.DialogButtonReplyType
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.view.common.screens.DialogListener
import com.hyeeyoung.wishboard.view.common.screens.TwoButtonDialogFragment
import com.hyeeyoung.wishboard.viewmodel.FolderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderMoreDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: DialogFolderMoreBinding
    private var folderInfo: Bundle? = null
    private val viewModel: FolderViewModel by activityViewModels()
    private var folderItem: FolderItem? = null
    private var position: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFolderMoreBinding.inflate(inflater, container, false)
        folderInfo = arguments

        arguments?.let {
            (it[ARG_FOLDER_ITEM] as? FolderItem)?.let { folder -> folderItem = folder }
            (it[ARG_FOLDER_POSITION] as? Int)?.let { position -> this.position = position }
        }

        addListener()
        addObservers()

        return binding.root
    }

    private fun addListener() {
        binding.update.setOnClickListener {
            dialog?.dismiss()
            val dialog = FolderAddDialogFragment()
            dialog.arguments = folderInfo
            dialog.show(requireActivity().supportFragmentManager, FolderAddDialogFragment.TAG)
        }
        binding.delete.setOnClickListener {
            dismiss()
            showFolderDeleteDialog()
        }
        binding.close.setOnClickListener {
            dismiss()
        }
    }

    private fun addObservers() {
        viewModel.getIsCompleteDeletion().observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                dismiss()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.folder_delete_toast_text),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetCompleteDeletion()
            }
        }
    }

    private fun showFolderDeleteDialog() {
        val dialog = TwoButtonDialogFragment(
            getString(R.string.folder_delete_dialog_title),
            getString(R.string.folder_delete_dialog_detail),
            getString(R.string.delete)
        ).apply {
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: DialogButtonReplyType) {
                    if (clicked == DialogButtonReplyType.YES) {
                        viewModel.deleteFolder(folderItem, position)
                    }
                    dismiss()
                }
            })
        }
        dialog.show(parentFragmentManager, "FolderDeleteDialog")
    }

    companion object {
        private const val TAG = "FolderMoreDialogFragment"
        private const val ARG_FOLDER_ITEM = "folderItem"
        private const val ARG_FOLDER_POSITION = "folderPosition"
    }
}