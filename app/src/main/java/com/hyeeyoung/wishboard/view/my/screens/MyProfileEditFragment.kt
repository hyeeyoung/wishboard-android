package com.hyeeyoung.wishboard.view.my.screens

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentProfileEditBinding
import com.hyeeyoung.wishboard.viewmodel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint

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

        addListeners()
        addObservers()

        return binding.root
    }

    private fun addListeners() {
        binding.profileImageContainer.setOnClickListener {
            requestStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        binding.complete.setOnClickListener {
            viewModel.updateUserNickname()
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
                viewModel.resetUserInfo()
                findNavController().popBackStack()
            }
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