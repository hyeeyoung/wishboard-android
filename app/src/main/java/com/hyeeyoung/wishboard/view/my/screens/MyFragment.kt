package com.hyeeyoung.wishboard.view.my.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentMyBinding
import com.hyeeyoung.wishboard.model.common.DialogButtonReplyType
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.loadProfileImage
import com.hyeeyoung.wishboard.view.common.screens.DialogListener
import com.hyeeyoung.wishboard.view.common.screens.TwoButtonDialogFragment
import com.hyeeyoung.wishboard.view.common.screens.WebViewActivity
import com.hyeeyoung.wishboard.view.sign.screens.SignActivity
import com.hyeeyoung.wishboard.viewmodel.MyViewModel
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
        binding.lifecycleOwner = this@MyFragment

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
        binding.opensourceLicense.setOnClickListener {
            moveWebViewActivity(
                "file:///android_asset/www/opensource_license.html",
                R.string.my_section_sub_title_opensource_license
            )
        }

        binding.logout.setOnClickListener {
            showLogoutDialog()
        }
        binding.exit.setOnClickListener {
            showMembershipExitDialog()
        }
        binding.profileImageContainer.setOnClickListener {
            findNavController().navigateSafe(R.id.action_my_to_profile_edit)
        }
    }

    private fun addObservers() {
        viewModel.getUserProfileImage().observe(viewLifecycleOwner) { profileImage ->
            if (profileImage == null) return@observe
            loadProfileImage(lifecycleScope, profileImage, binding.profileImage)
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
            getString(R.string.my_section_sub_title_logout)
        ).apply {
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: String) {
                    if (clicked == DialogButtonReplyType.YES.name) {
                        viewModel.signOut()
                        startActivity(Intent(requireContext(), SignActivity::class.java))
                        requireActivity().finish()
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
        private const val TAG = "MyFragment"
        private const val ARG_WEB_VIEW_LINK = "link"
        private const val ARG_WEB_VIEW_TITLE = "title"
    }
}