package com.hyeeyoung.wishboard.presentation.my.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentMyBinding
import com.hyeeyoung.wishboard.presentation.common.screens.TwoButtonDialogFragment
import com.hyeeyoung.wishboard.presentation.common.screens.WebViewActivity
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.presentation.my.MyViewModel
import com.hyeeyoung.wishboard.presentation.sign.screens.SignActivity
import com.hyeeyoung.wishboard.presentation.wishitem.WishItemStatus
import com.hyeeyoung.wishboard.util.DialogListener
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFragment : Fragment() {
    private lateinit var binding: FragmentMyBinding
    private val viewModel: MyViewModel by hiltNavGraphViewModels(R.id.my_nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchUserInfo()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initializeView()
        addListeners()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        binding.profileImage.clipToOutline = true
    }

    private fun addListeners() {
        binding.notiSwitch.setOnClickListener {
            viewModel.updatePushState()
        }
        binding.contactUs.setOnClickListener {
            val email = Intent(Intent.ACTION_SEND)
            email.type = "plain/text"
            email.putExtra(Intent.EXTRA_EMAIL, arrayOf("wishboard2022@gmail.com"))
            startActivity(email)
        }
        binding.howToUse.setOnClickListener {
            // 노션에서 보안 문제로 노션 페이지를 웹뷰를 띄울 수 없다고 함. 임의로 새창에서 띄우는 것으로 수정
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://hushed-bolt-fd4.notion.site/383c308f256f4f189b7c0b68a8f68d9f")
                )
            )
        }
        binding.terms.setOnClickListener {
            moveWebViewActivity(
                "https://www.wishboard.xyz/terms.html",
                R.string.my_section_sub_title_terms
            )
        }
        binding.personalInfo.setOnClickListener {
            moveWebViewActivity(
                "https://www.wishboard.xyz/privacy-policy.html",
                R.string.my_section_sub_title_personal_info
            )
        }
        binding.opensourceLicense.setOnClickListener {
            moveWebViewActivity(
                "https://www.wishboard.xyz/opensource.html",
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
        viewModel.getUserProfileImage().observe(viewLifecycleOwner) { profileImage ->
            if (profileImage == null) return@observe
            binding.profileImage.load(profileImage)
            return@observe
        }

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
            (it[ARG_PROFILE_UPDATE_STATUS] as? WishItemStatus)?.let { status ->
                if (status == WishItemStatus.MODIFIED) { // TODO 범용 status로 변경
                    viewModel.fetchUserInfo()
                }
            }
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

    companion object {
        private const val ARG_WEB_VIEW_LINK = "link"
        private const val ARG_WEB_VIEW_TITLE = "title"
        private const val ARG_PROFILE_UPDATE_INFO = "profileUpdateInfo"
        private const val ARG_PROFILE_UPDATE_STATUS = "profileUpdateStatus"
    }
}