package com.hyeeyoung.wishboard.presentation.sign.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentSignInEmailBinding
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.presentation.sign.SignViewModel
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.showKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInEmailFragment : Fragment() {
    private lateinit var binding: FragmentSignInEmailBinding
    private val viewModel: SignViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInEmailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        addObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showKeyboard(requireContext(), binding.emailInput, true)
    }

    private fun addObservers() {
        viewModel.isCompletedSendMail().observe(viewLifecycleOwner) { isCompleted ->
            if (isCompleted == true) {
                findNavController().navigateSafe(R.id.action_email_to_verification_code)
                viewModel.setCompletedSendMail(false)
            }
        }

//        viewModel.isCompletedSendMail().observe(viewLifecycleOwner) { isCompleted ->
//            // 재요청 버튼 클릭 시 인증코드 EditText로 커서 이동
//            if (isCompleted == true) {
//                binding.verificationCodeInput.requestFocus()
//            }
//        }

        viewModel.getSignProcessStatus().observe(viewLifecycleOwner) {
            when (it) {
                ProcessStatus.IDLE -> {
                    binding.loadingLottie.visibility = View.GONE
                }
                ProcessStatus.IN_PROGRESS -> {
                    binding.loadingLottie.visibility = View.VISIBLE
                    binding.loadingLottie.playAnimation()
                }
            }
        }
    }
}