package com.hyeeyoung.wishboard.presentation.my.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentPasswordChangeBinding
import com.hyeeyoung.wishboard.presentation.base.screen.NetworkFragment
import com.hyeeyoung.wishboard.presentation.my.MyViewModel
import com.hyeeyoung.wishboard.util.UiState
import com.hyeeyoung.wishboard.designsystem.component.CustomSnackbar
import com.hyeeyoung.wishboard.util.extension.collectFlow
import com.hyeeyoung.wishboard.util.showKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPasswordChangeFragment :
    NetworkFragment<FragmentPasswordChangeBinding>(R.layout.fragment_password_change) {
    private val viewModel: MyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showKeyboard(requireContext(), binding.passwordInput, true)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        addListeners()
        collectData()
    }

    private fun addListeners() {
        binding.topAppBar.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun collectData() {
        collectFlow(viewModel.passwordChangeState) { changeState ->
            when (changeState) {
                is UiState.Success -> {
                    with(requireContext()) {
                        CustomSnackbar.make(
                            binding.layout,
                            getString(R.string.my_password_change_snackbar_text),
                            false
                        ).show()
                        findNavController().popBackStack()
                    }
                }
                else -> {}
            }
        }
    }
}