package com.hyeeyoung.wishboard.view.sign

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hyeeyoung.wishboard.MainActivity
import com.hyeeyoung.wishboard.databinding.FragmentSignUpPasswordBinding
import com.hyeeyoung.wishboard.viewmodel.SignViewModel

class SignUpPasswordFragment : Fragment() {
    private lateinit var binding: FragmentSignUpPasswordBinding
    private val viewModel: SignViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpPasswordBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        init()

        return binding.root
    }

    private fun init() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

}