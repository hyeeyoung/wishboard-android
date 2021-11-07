package com.hyeeyoung.wishboard.view.folder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hyeeyoung.wishboard.databinding.FragmentFolderBinding

class FolderFragment : Fragment() {
    private lateinit var binding: FragmentFolderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FolderFragment().apply {

            }
    }
}