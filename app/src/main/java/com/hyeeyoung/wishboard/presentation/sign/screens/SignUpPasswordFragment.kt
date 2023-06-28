package com.hyeeyoung.wishboard.presentation.sign.screens

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentSignUpPasswordBinding
import com.hyeeyoung.wishboard.presentation.common.screens.WebViewActivity
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.presentation.main.MainActivity
import com.hyeeyoung.wishboard.presentation.sign.SignViewModel
import com.hyeeyoung.wishboard.util.BaseFragment
import com.hyeeyoung.wishboard.util.showKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignUpPasswordFragment :
    BaseFragment<FragmentSignUpPasswordBinding>(R.layout.fragment_sign_up_password) {
    private val viewModel: SignViewModel by hiltNavGraphViewModels(R.id.sign_nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.resetRegistrationPassword()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        showKeyboard(requireContext(), binding.passwordInput, true)
        initializeView()
        addListeners()
        addObservers()
    }

    private fun addListeners() {
        binding.topAppBar.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initializeView() {
        val clickableSpanTerms = object : ClickableSpan() {
            override fun onClick(widget: View) {
                moveWebViewActivity(
                    "https://youngjinc.notion.site/f0e9dae6172b47659a9e01e55db8e2e3?pvs=4",
                    R.string.my_section_sub_title_terms
                )
            }
        }
        val clickableSpanPersonalInfo = object : ClickableSpan() {
            override fun onClick(widget: View) {
                moveWebViewActivity(
                    "https://youngjinc.notion.site/5c3da8efa7744e08a94d06fca2eecfc6?pvs=4",
                    R.string.my_section_sub_title_personal_info
                )
            }
        }

        with(binding.signTerm) {
            movementMethod = LinkMovementMethod.getInstance()
            val spannable = text.toSpannable()
            spannable[5..9] = clickableSpanTerms
            spannable[5..9] =
                ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.green_700, null))
            spannable[11..20] = clickableSpanPersonalInfo
            spannable[11..20] =
                ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.green_700, null))
        }
    }

    private fun addObservers() {
        viewModel.getCompletedSignUp().observe(viewLifecycleOwner) { isCompleted ->
            if (isCompleted) {
                moveToMain()
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

    private fun moveToMain() {
        Intent(this.context, MainActivity::class.java).apply {
            putExtra(ARG_SUCCESS_SIGN_UP, true)
        }.let {
            startActivity(it)
            requireActivity().finish()
        }
    }

    private fun moveWebViewActivity(link: String, titleRes: Int) {
        Intent(requireContext(), WebViewActivity::class.java).apply {
            putExtra(ARG_WEB_VIEW_LINK, link)
            putExtra(ARG_WEB_VIEW_TITLE, context?.getString(titleRes))
        }.let {
            startActivity(it)
        }
    }

    companion object {
        private const val ARG_SUCCESS_SIGN_UP = "successSignup"
        private const val ARG_WEB_VIEW_LINK = "link"
        private const val ARG_WEB_VIEW_TITLE = "title"
    }
}