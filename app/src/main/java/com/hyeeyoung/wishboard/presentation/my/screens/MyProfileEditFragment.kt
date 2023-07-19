package com.hyeeyoung.wishboard.presentation.my.screens

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentProfileEditBinding
import com.hyeeyoung.wishboard.designsystem.component.CustomSnackbar
import com.hyeeyoung.wishboard.presentation.my.MyViewModel
import com.hyeeyoung.wishboard.presentation.wishitem.WishItemStatus
import com.hyeeyoung.wishboard.util.BaseFragment
import com.hyeeyoung.wishboard.util.UiState
import com.hyeeyoung.wishboard.util.extension.collectFlow
import com.hyeeyoung.wishboard.util.extension.showPhotoDialog
import com.hyeeyoung.wishboard.util.showKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProfileEditFragment :
    BaseFragment<FragmentProfileEditBinding>(R.layout.fragment_profile_edit) {
    private val viewModel: MyViewModel by hiltNavGraphViewModels(R.id.my_nav_graph)
    private var photoUri: Uri? = null

    private val requestSelectPicture =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) viewModel.setSelectedUserProfileImage(uri)
        }

    private val requestCamera =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                photoUri = viewModel.createCameraImageUri()
                takePicture.launch(photoUri)
            }
        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && (photoUri != null)) viewModel.setSelectedUserProfileImage(photoUri!!)
        }

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
        collectData()
    }

    private fun addListeners() {
        binding.profileImageContainer.setOnClickListener {
            showPhotoDialog(requestCamera, requestSelectPicture)
        }
        binding.topAppBar.back.setOnClickListener {
            findNavController().popBackStack()
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

    companion object {
        private const val ARG_PROFILE_UPDATE_INFO = "profileUpdateInfo"
        private const val ARG_PROFILE_UPDATE_STATUS = "profileUpdateStatus"
    }
}