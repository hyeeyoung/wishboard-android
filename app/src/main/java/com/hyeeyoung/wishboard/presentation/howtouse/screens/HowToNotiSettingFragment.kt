package com.hyeeyoung.wishboard.presentation.howtouse.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hyeeyoung.wishboard.databinding.FragmentHowToNotiSettingBinding

class HowToNotiSettingFragment : Fragment() {
    private lateinit var binding: FragmentHowToNotiSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHowToNotiSettingBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        private const val TAG = "HowToNotiSettingFragment"
    }
}
