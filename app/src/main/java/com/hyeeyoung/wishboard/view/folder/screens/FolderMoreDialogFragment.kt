package com.hyeeyoung.wishboard.view.folder.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyeeyoung.wishboard.databinding.DialogFolderMoreBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderMoreDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: DialogFolderMoreBinding
    private var folderInfo: Bundle? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFolderMoreBinding.inflate(inflater, container, false)
        folderInfo = arguments

        addListener()

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
            dialog?.dismiss()
            val dialog = FolderDeleteDialogFragment()
            dialog.arguments = folderInfo
            dialog.show(requireActivity().supportFragmentManager, FolderDeleteDialogFragment.TAG)
        }
    }

    companion object {
        private const val TAG = "FolderMoreDialogFragment"
    }
}