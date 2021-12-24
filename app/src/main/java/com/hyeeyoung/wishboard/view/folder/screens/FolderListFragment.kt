package com.hyeeyoung.wishboard.view.folder.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hyeeyoung.wishboard.databinding.FragmentFolderListBinding

class FolderListFragment : Fragment() {
    private lateinit var binding: FragmentFolderListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderListBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
       private const val TAG = "FolderListFragment"
    }
}