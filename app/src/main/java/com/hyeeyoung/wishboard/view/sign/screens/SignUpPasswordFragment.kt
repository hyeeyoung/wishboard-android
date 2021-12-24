package com.hyeeyoung.wishboard.view.sign.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hyeeyoung.wishboard.MainActivity
import com.hyeeyoung.wishboard.databinding.FragmentSignUpPasswordBinding
import com.hyeeyoung.wishboard.viewmodel.SignViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpPasswordFragment : Fragment() {
    private lateinit var binding: FragmentSignUpPasswordBinding
    private val viewModel: SignViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpPasswordBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        setListeners()
        setObservers()

        return binding.root
    }

    private fun setListeners() {
        viewModel.getValidPasswordFormat().observe(viewLifecycleOwner) {
            binding.next.isEnabled = it
        }
    }

    private fun setObservers() {
        viewModel.getCompletedSignUp().observe(viewLifecycleOwner) { isCompleted ->
            if (isCompleted) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
        }
    }
}