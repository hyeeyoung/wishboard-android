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
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentMyBinding
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.loadProfileImage
import com.hyeeyoung.wishboard.util.prefs
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
        prefs?.getCheckedPushNoti()?.let {
            binding.notiSwitch.isChecked = it
        }
        binding.notiSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updatePushNotiSettings(isChecked)
        }
        binding.logout.setOnClickListener {
            viewModel.signOut()
            startActivity(Intent(requireContext(), SignActivity::class.java))
            requireActivity().finish()
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
    }
}