package com.hyeeyoung.wishboard.view.sign

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.hyeeyoung.wishboard.MainActivity
import com.hyeeyoung.wishboard.databinding.FragmentSignInBinding
import com.hyeeyoung.wishboard.viewmodel.SignViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private val viewModel: SignViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        setObservers()

        return binding.root
    }

    private fun setObservers() {
        viewModel.getCompletedSignIn().observe(viewLifecycleOwner) { isCompleted ->
            when (isCompleted) {
                true -> {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                }
                false -> {
                    // TODO 에러케이스에 따라 에러메세지 분리 및 스낵바 커스텀 필요
                    Snackbar.make(binding.layout, "잘못된 아이디 또는 패스워드 입니다", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}