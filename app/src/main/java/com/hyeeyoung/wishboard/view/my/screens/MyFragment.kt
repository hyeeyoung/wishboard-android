package com.hyeeyoung.wishboard.view.my.screens

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.hyeeyoung.wishboard.databinding.FragmentMyBinding
import com.hyeeyoung.wishboard.view.sign.screens.SignActivity
import com.hyeeyoung.wishboard.viewmodel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFragment : Fragment() {
    private lateinit var binding: FragmentMyBinding
    private val viewModel: MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@MyFragment

        addListeners()

        return binding.root
    }

    private fun addListeners() {
        binding.notiSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updatePushNotiSettings(isChecked)
        }
        binding.logoutBtn.setOnClickListener {
            viewModel.signOut()
            startActivity(Intent(requireContext(), SignActivity::class.java))
            requireActivity().finish()
        }
    }
}