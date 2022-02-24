package com.hyeeyoung.wishboard.view.my.screens

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.hyeeyoung.wishboard.databinding.FragmentProfileEditBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProfileEditFragment : Fragment() {
    private lateinit var binding: FragmentProfileEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)

        addListeners()

        return binding.root
    }

    private fun addListeners() {
        binding.profileImageContainer.setOnClickListener {
            requestStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private val requestStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                // TODO 갤러리 프래그먼트와 네비게이션 그래프 불일치, 통일 필요
                // findNavController().navigateSafe(R.id.action_my_profile_edit_to_gallery_image)
            }
        }

    companion object {
        private const val TAG = "ProfileEditFragment"
    }
}