package com.hyeeyoung.wishboard.view.sign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hyeeyoung.wishboard.databinding.FragmentSignUpEmailBinding
import com.hyeeyoung.wishboard.viewmodel.SignViewModel

class SignUpEmailFragment : Fragment() {
    private lateinit var binding: FragmentSignUpEmailBinding
    private val viewModel: SignViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpEmailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        return binding.root
    }
}