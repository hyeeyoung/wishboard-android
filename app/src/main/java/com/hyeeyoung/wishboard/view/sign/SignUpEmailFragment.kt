package com.hyeeyoung.wishboard.view.sign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hyeeyoung.wishboard.databinding.FragmentSignUpEmailBinding

class SignUpEmailFragment : Fragment() {
    private lateinit var binding: FragmentSignUpEmailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpEmailBinding.inflate(inflater, container, false)

        return binding.root
    }
}