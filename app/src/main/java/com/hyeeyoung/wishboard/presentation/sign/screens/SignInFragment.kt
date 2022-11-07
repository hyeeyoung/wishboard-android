package com.hyeeyoung.wishboard.presentation.sign.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.presentation.main.MainActivity
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentSignInBinding
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.showKeyboard
import com.hyeeyoung.wishboard.presentation.sign.SignViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private val viewModel: SignViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        addListeners()
        addObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showKeyboard(requireContext(), binding.emailInput, true)
    }

    private fun addListeners() {
        binding.forgotYourPassword.setOnClickListener {
            findNavController().navigateSafe(R.id.action_sign_in_to_sign_in_email)
        }
    }

    private fun addObservers() {
        viewModel.getCompletedSignIn().observe(viewLifecycleOwner) { isCompleted ->
            when (isCompleted) {
                true -> {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                }
                else -> {
                    // TODO 에러케이스에 따라 에러메세지 분리 및 스낵바 커스텀 필요
                    CustomSnackbar.make(binding.layout, getString(R.string.sign_in_failed_snackbar_text), false).show()
                }
            }
        }
        viewModel.getSignProcessStatus().observe(viewLifecycleOwner) {
            when (it) {
                ProcessStatus.IDLE -> {
                    binding.loadingLottie.visibility = View.GONE
                }
                ProcessStatus.IN_PROGRESS -> {
                    binding.loadingLottie.visibility = View.VISIBLE
                    binding.loadingLottie.playAnimation()
                }
                else -> {}
            }
        }
    }
}