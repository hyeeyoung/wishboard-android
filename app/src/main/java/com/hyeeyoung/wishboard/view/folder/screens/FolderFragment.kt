package com.hyeeyoung.wishboard.view.folder.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentFolderBinding
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.util.extension.navigateSafe

class FolderFragment : Fragment() {
    private lateinit var binding: FragmentFolderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFolderBinding.inflate(inflater, container, false)

        addListeners()
        addObservers()

        return binding.root
    }

    private fun addListeners() {
        binding.newFolder.setOnClickListener {
            findNavController().navigateSafe(R.id.action_folder_to_folder_add_dialog)
        }
    }

    private fun addObservers() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<FolderItem>(
            ARG_FOLDER_ITEM
        )?.observe(viewLifecycleOwner) {
        }
    }

    companion object {
        private const val TAG = "FolderFragment"
        private const val ARG_FOLDER_ITEM = "folderItem"
    }
}