package com.hyeeyoung.wishboard.presentation.my.screens

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentProfileEditBinding
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.presentation.my.MyViewModel
import com.hyeeyoung.wishboard.presentation.wishitem.WishItemStatus
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.safeLet
import com.hyeeyoung.wishboard.util.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MyProfileEditFragment : Fragment() {
    private lateinit var binding: FragmentProfileEditBinding
    private val viewModel: MyViewModel by hiltNavGraphViewModels(R.id.my_nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.resetUserInfo()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // TODO need refactoring 기존 이미지 보여주기
        binding.profileImage.load(viewModel.getUserProfileImage().value)

        // 갤러리에서 선택한 이미지 전달받기
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(
            ARG_IMAGE_INFO
        )?.observe(viewLifecycleOwner) {
            safeLet(
                it[ARG_IMAGE_URI] as? Uri, it[ARG_IMAGE_FILE] as? File
            ) { imageUri, imageFile ->
                binding.profileImage.load(imageUri)
                viewModel.setSelectedUserProfileImage(imageUri, imageFile)
            }
            it.clear()
        }

        initializeView()
        addListeners()
        addObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showKeyboard(requireContext(), binding.nicknameInput, true)
    }

    private fun initializeView() {
        binding.profileImage.clipToOutline = true
    }

    private fun addListeners() {
        binding.profileImageContainer.setOnClickListener {
            requestStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        binding.complete.setOnClickListener {
            viewModel.updateUserInfo()
        }
    }

    private fun addObservers() {
        viewModel.getCompleteUpdateUserInfo().observe(viewLifecycleOwner) { isComplete ->
            if (isComplete == true) {
                CustomSnackbar.make(
                    binding.layout, getString(R.string.my_profile_edit_completion_snackbar_text)
                ).show()
                moveToPrevious()
            }
        }

        viewModel.getProfileEditStatus().observe(viewLifecycleOwner) {
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

    private fun moveToPrevious() {
        val navController = findNavController()
        navController.previousBackStackEntry?.savedStateHandle?.set(
            ARG_PROFILE_UPDATE_INFO, bundleOf(
                ARG_PROFILE_UPDATE_STATUS to WishItemStatus.MODIFIED,
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
        private const val ARG_IMAGE_INFO = "imageInfo"
        private const val ARG_IMAGE_URI = "imageUri"
        private const val ARG_IMAGE_FILE = "imageFile"
        private const val ARG_PROFILE_UPDATE_INFO = "profileUpdateInfo"
        private const val ARG_PROFILE_UPDATE_STATUS = "profileUpdateStatus"
    }
}