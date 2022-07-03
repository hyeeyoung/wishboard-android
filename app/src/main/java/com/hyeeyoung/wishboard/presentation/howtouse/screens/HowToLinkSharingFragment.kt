package com.hyeeyoung.wishboard.presentation.howtouse.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hyeeyoung.wishboard.databinding.FragmentHowToLinkSharingBinding

class HowToLinkSharingFragment : Fragment() {
    private lateinit var binding: FragmentHowToLinkSharingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHowToLinkSharingBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        private const val TAG = "HowToLinkSharingFragment"
    }
}
