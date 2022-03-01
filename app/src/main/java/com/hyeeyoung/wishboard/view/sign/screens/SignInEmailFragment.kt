package com.hyeeyoung.wishboard.view.sign.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.hyeeyoung.wishboard.MainActivity
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentSignInEmailBinding
import com.hyeeyoung.wishboard.viewmodel.SignViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInEmailFragment : Fragment() {
    private lateinit var binding: FragmentSignInEmailBinding
    private val viewModel: SignViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInEmailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@SignInEmailFragment

        addObservers()

        return binding.root
    }

    private fun addObservers() {
        viewModel.getCompletedSignIn().observe(viewLifecycleOwner) { isCompleted ->
            when (isCompleted) {
                true -> {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }
}