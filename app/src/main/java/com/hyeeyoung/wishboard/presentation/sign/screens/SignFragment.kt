package com.hyeeyoung.wishboard.presentation.sign.screens

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentSignBinding
import com.hyeeyoung.wishboard.util.BaseFragment
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignFragment : BaseFragment<FragmentSignBinding>(R.layout.fragment_sign) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addListeners()
    }

    private fun addListeners() {
        binding.signIn.setOnClickListener {
            findNavController().navigateSafe(R.id.action_sign_to_sign_in)
        }
        binding.signUp.setOnClickListener {
            findNavController().navigateSafe(R.id.action_sign_to_email)
        }
    }
}