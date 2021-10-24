package com.hyeeyoung.wishboard.view.noti

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hyeeyoung.wishboard.databinding.FragmentNotiBinding

class NotiFragment : Fragment() {
    private lateinit var binding: FragmentNotiBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotiBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotiFragment().apply {

            }
    }
}