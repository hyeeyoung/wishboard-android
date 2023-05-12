package com.hyeeyoung.wishboard.presentation.sign.screens

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentSignUpEmailBinding
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.presentation.sign.SignViewModel
import com.hyeeyoung.wishboard.util.BaseFragment
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.showKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpEmailFragment :
    BaseFragment<FragmentSignUpEmailBinding>(R.layout.fragment_sign_up_email) {
    private val viewModel: SignViewModel by hiltNavGraphViewModels(R.id.sign_nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.resetRegistrationEmail()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        showKeyboard(requireContext(), binding.emailInput, true)
        addObservers()
    }

    private fun addObservers() {
        viewModel.isUnregisteredUser().observe(viewLifecycleOwner) { isUnregistered ->
            if (isUnregistered == true) {
                findNavController().navigateSafe(R.id.action_email_to_password)
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