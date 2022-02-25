package com.hyeeyoung.wishboard.view.my.screens

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentProfileEditBinding
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.loadProfileImage
import com.hyeeyoung.wishboard.util.safeLet
import com.hyeeyoung.wishboard.viewmodel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MyProfileEditFragment : Fragment() {
    private lateinit var binding: FragmentProfileEditBinding
    private val viewModel: MyViewModel by hiltNavGraphViewModels(R.id.my_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@MyProfileEditFragment

        viewModel.resetUserInfo()

        // 갤러리에서 선택한 이미지 전달받기
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(
            ARG_IMAGE_INFO
        )?.observe(viewLifecycleOwner) {
            safeLet(
                it[ARG_IMAGE_URI] as? Uri, it[ARG_IMAGE_FILE] as? File
            ) { imageUri, imageFile ->
                Glide.with(requireContext()).load(imageUri).into(binding.profileImage)
                viewModel.setSelectedUserProfileImage(imageUri, imageFile)
            }
        }

        initializeView()
        addListeners()
        addObservers()

        return binding.root
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
                Toast.makeText(
                    context,
                    getString(R.string.my_profile_edit_completion_toast_Text),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            }
        }

        // 갤러리에서 선택한 사진이 없는 경우, 기존 프로필 사진 보여주기
        viewModel.getUserProfileImageUri().observe(viewLifecycleOwner) { uri ->
            if (uri != null) return@observe
            viewModel.getUserProfileImage().value?.let { profileImage ->
                loadProfileImage(lifecycleScope, profileImage, binding.profileImage)
            }
            return@observe
        }
    }

    private val requestStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                // TODO 갤러리 프래그먼트와 네비게이션 그래프 불일치, 통일 필요
                findNavController().navigateSafe(R.id.action_my_profile_edit_to_gallery_image)
            }
        }

    companion object {
        private const val TAG = "ProfileEditFragment"
        private const val ARG_IMAGE_INFO = "imageInfo"
        private const val ARG_IMAGE_URI = "imageUri"
        private const val ARG_IMAGE_FILE = "imageFile"
    }
}