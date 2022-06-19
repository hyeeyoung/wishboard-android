package com.hyeeyoung.wishboard.presentation.howtouse.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hyeeyoung.wishboard.databinding.FragmentHowToFolderSettingBinding

class HowToFolderSettingFragment : Fragment() {
    private lateinit var binding: FragmentHowToFolderSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHowToFolderSettingBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        private const val TAG = "HowToFolderSettingFragment"
    }
}