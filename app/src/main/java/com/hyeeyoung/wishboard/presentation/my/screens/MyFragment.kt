package com.hyeeyoung.wishboard.presentation.my.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentMyBinding
import com.hyeeyoung.wishboard.presentation.base.screen.NetworkFragment
import com.hyeeyoung.wishboard.presentation.common.screens.TwoButtonDialogFragment
import com.hyeeyoung.wishboard.presentation.common.screens.WebViewActivity
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.presentation.my.MyViewModel
import com.hyeeyoung.wishboard.presentation.sign.screens.SignActivity
import com.hyeeyoung.wishboard.presentation.wishitem.WishItemStatus
import com.hyeeyoung.wishboard.util.DialogListener
import com.hyeeyoung.wishboard.util.UiState
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
import com.hyeeyoung.wishboard.util.extension.collectFlow
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.extension.safeValueOf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine

@AndroidEntryPoint
class MyFragment : NetworkFragment<FragmentMyBinding>(R.layout.fragment_my) {
    private val viewModel: MyViewModel by hiltNavGraphViewModels(R.id.my_nav_graph)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initializeView()
        addListeners()
        addObservers()
        collectData()
    }

    private fun initializeView() {
        binding.version.text = BuildConfig.VERSION_NAME
    }

    private fun addListeners() {
        binding.notiSwitch.setOnClickListener {
            viewModel.updatePushState()
        }
        binding.changePassword.setOnClickListener {
            findNavController().navigateSafe(R.id.action_my_to_password_change)
        }
        binding.contactUs.setOnClickListener {
            val email = Intent(Intent.ACTION_SEND)
            email.type = "plain/text"
            email.putExtra(Intent.EXTRA_EMAIL, arrayOf("wishboard2022@gmail.com"))
            startActivity(email)
        }
        binding.howToUse.setOnClickListener {
            moveWebViewActivity(
                "https://youngjinc.notion.site/1fb5fba5448941c7bac0dd72b1a51871?pvs=4",
                R.string.my_section_sub_title_how_to_use
            )
        }
        binding.terms.setOnClickListener {
            moveWebViewActivity(
                "https://youngjinc.notion.site/f0e9dae6172b47659a9e01e55db8e2e3?pvs=4",
                R.string.my_section_sub_title_terms
            )
        }
        binding.personalInfo.setOnClickListener {
            moveWebViewActivity(
                "https://youngjinc.notion.site/5c3da8efa7744e08a94d06fca2eecfc6?pvs=4",
                R.string.my_section_sub_title_personal_info
            )
        }
        binding.opensourceLicense.setOnClickListener {
            moveWebViewActivity(
                "https://youngjinc.notion.site/48d95a3554074db8af829bf5af28c842?pvs=4",
                R.string.my_section_sub_title_opensource_license
            )
        }
        binding.logout.setOnClickListener {
            showLogoutDialog()
        }
        binding.exit.setOnClickListener {
            showMembershipExitDialog()
        }
        binding.profileEdit.setOnClickListener {
            findNavController().navigateSafe(R.id.action_my_to_profile_edit)
        }
    }

    private fun addObservers() {
        viewModel.getCompleteDeleteUser().observe(viewLifecycleOwner) { isComplete ->
            if (isComplete == true) {
                CustomSnackbar.make(binding.layout,
                    getString(R.string.my_delete_user_snackbar_text),
                    true,
                    object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            startActivity(Intent(requireContext(), SignActivity::class.java))
                            requireActivity().finish()
                        }
                    }).show()
            }
        }

        viewModel.isCompleteLogout.observe(viewLifecycleOwner) { isComplete ->
            if (isComplete == true) {
                startActivity(Intent(requireContext(), SignActivity::class.java))
                requireActivity().finish()
            }
        }

        // 프로필 수정 후 프로필 편집 뷰에서 마이페이지 복귀 시 프로필 정보 ui 업데이트
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(
            ARG_PROFILE_UPDATE_INFO
        )?.observe(viewLifecycleOwner) {
            val isModified = safeValueOf<WishItemStatus>(it.getString(ARG_PROFILE_UPDATE_STATUS))
            if (isModified == WishItemStatus.MODIFIED) // TODO 범용 status로 변경
                viewModel.requestUserInfoFetchState()
            it.clear()
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

    private fun showLogoutDialog() {
        val dialog = TwoButtonDialogFragment(
            getString(R.string.my_section_sub_title_logout),
            getString(R.string.my_logout_dialog_description),
            getString(R.string.my_section_sub_title_logout),
            getString(R.string.cancel)
        ).apply {
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: String) {
                    if (clicked == DialogButtonReplyType.YES.name) {
                        viewModel.signOut()
                    }
                    dismiss()
                }
            })
        }
        dialog.show(parentFragmentManager, "LogoutDialog")
    }

    private fun showMembershipExitDialog() {
        val dialog = MyExitDialogFragment(
        ).apply {
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: String) {
                    if (clicked == DialogButtonReplyType.YES.name) {
                        viewModel.deleteUserAccount()
                        dismiss()
                    }
                }
            })
        }
        dialog.show(parentFragmentManager, "MembershipExitDialog")
    }

    private fun collectData() {
        collectFlow(combine(isConnected, viewModel.userInfoFetchState) { isConnected, isSuccessful ->
            isConnected && isSuccessful !is UiState.Success
        }) { shouldFetch ->
            if (shouldFetch) viewModel.fetchUserInfo()
        }
    }

    companion object {
        private const val ARG_WEB_VIEW_LINK = "link"
        private const val ARG_WEB_VIEW_TITLE = "title"
        private const val ARG_PROFILE_UPDATE_INFO = "profileUpdateInfo"
        private const val ARG_PROFILE_UPDATE_STATUS = "profileUpdateStatus"
    }
}