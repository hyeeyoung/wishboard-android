package com.hyeeyoung.wishboard.presentation.my.screens

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentProfileEditBinding
import com.hyeeyoung.wishboard.presentation.my.MyViewModel
import com.hyeeyoung.wishboard.presentation.wishitem.WishItemStatus
import com.hyeeyoung.wishboard.util.BaseFragment
import com.hyeeyoung.wishboard.util.UiState
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
import com.hyeeyoung.wishboard.util.extension.collectFlow
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.showKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProfileEditFragment :
    BaseFragment<FragmentProfileEditBinding>(R.layout.fragment_profile_edit) {
    private val viewModel: MyViewModel by hiltNavGraphViewModels(R.id.my_nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.resetUserInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        showKeyboard(requireContext(), binding.nicknameInput, true)
        addListeners()
        addObserver()
        collectData()
    }

    private fun addListeners() {
        binding.profileImageContainer.setOnClickListener {
            requestStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        binding.topAppBar.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun addObserver() {
        // 갤러리에서 선택한 이미지 전달받기
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
            ARG_IMAGE_URI
        )?.observe(viewLifecycleOwner) { uri ->
            viewModel.setSelectedUserProfileImage(Uri.parse(uri))
        }
    }

    private fun collectData() {
        collectFlow(viewModel.userInfoUpdateState) {
            when (it) {
                is UiState.Success -> {
                    CustomSnackbar.make(
                        binding.root,
                        getString(R.string.my_profile_edit_completion_snackbar_text),
                        false
                    ).show()
                    moveToPrevious()
                }
                else -> {}
            }
        }
    }

    private fun moveToPrevious() {
        val navController = findNavController()
        navController.previousBackStackEntry?.savedStateHandle?.set(
            ARG_PROFILE_UPDATE_INFO, bundleOf(
                ARG_PROFILE_UPDATE_STATUS to WishItemStatus.MODIFIED.name,
            )
        )
        navController.popBackStack()
    }

    private val requestStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                // TODO 갤러리 프래그먼트와 네비게이션 그래프 불일치, 통일 필요
                findNavController().navigateSafe(R.id.action_my_profile_edit_to_gallery_image)
            }
        }

    companion object {
        private const val ARG_IMAGE_URI = "imageUri"
        private const val ARG_PROFILE_UPDATE_INFO = "profileUpdateInfo"
        private const val ARG_PROFILE_UPDATE_STATUS = "profileUpdateStatus"
    }
}