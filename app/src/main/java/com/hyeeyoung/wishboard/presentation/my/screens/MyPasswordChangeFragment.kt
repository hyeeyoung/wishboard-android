package com.hyeeyoung.wishboard.presentation.my.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentPasswordChangeBinding
import com.hyeeyoung.wishboard.presentation.my.MyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPasswordChangeFragment : Fragment() {
    private lateinit var binding: FragmentPasswordChangeBinding
    private val viewModel: MyViewModel by hiltNavGraphViewModels(R.id.my_nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.resetUserInfo()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordChangeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        addListeners()
        addObservers()

        return binding.root
    }

    private fun addListeners() {
        binding.complete.setOnClickListener {
            // TODO 비밀번호 변경 api 호출
        }
    }

    private fun addObservers() {

    }
}