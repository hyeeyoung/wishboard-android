package com.hyeeyoung.wishboard.presentation.sign.screens

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentSignUpPasswordBinding
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.util.showKeyboard
import com.hyeeyoung.wishboard.presentation.main.MainActivity
import com.hyeeyoung.wishboard.presentation.common.screens.WebViewActivity
import com.hyeeyoung.wishboard.presentation.sign.SignViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignUpPasswordFragment : Fragment() {
    private lateinit var binding: FragmentSignUpPasswordBinding
    private val viewModel: SignViewModel by hiltNavGraphViewModels(R.id.sign_nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.resetRegistrationPassword()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpPasswordBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@SignUpPasswordFragment

        initializeView()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        binding.signTerm.movementMethod = LinkMovementMethod.getInstance()

        val clickableSpanTerms = object : ClickableSpan() {
            override fun onClick(widget: View) {
                moveWebViewActivity(
                    "https://www.wishboard.xyz/terms.html",
                    R.string.my_section_sub_title_terms
                )
            }
        }
        val clickableSpanPersonalInfo = object : ClickableSpan() {
            override fun onClick(widget: View) {
                moveWebViewActivity(
                    "https://www.wishboard.xyz/privacy-policy.html",
                    R.string.my_section_sub_title_personal_info
                )
            }
        }

        (binding.signTerm.text as? Spannable)?.apply {
            setSpan(clickableSpanTerms, 5, 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            setSpan(ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.green_500, null)), 5, 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            setSpan(clickableSpanPersonalInfo, 11, 19, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            setSpan(ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.green_500, null)), 11, 19, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showKeyboard(requireContext(), binding.passwordInput, true)
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
        private const val TAG = "SignUpPasswordFragment"
        private const val ARG_SUCCESS_SIGN_UP = "successSignup"
        private const val ARG_WEB_VIEW_LINK = "link"
        private const val ARG_WEB_VIEW_TITLE = "title"
    }
}