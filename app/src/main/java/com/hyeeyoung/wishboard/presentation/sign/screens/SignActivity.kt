package com.hyeeyoung.wishboard.presentation.sign.screens

import android.os.Bundle
import androidx.activity.viewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.ActivitySignBinding
import com.hyeeyoung.wishboard.presentation.base.screen.NetworkActivity
import com.hyeeyoung.wishboard.presentation.sign.SignViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignActivity : NetworkActivity<ActivitySignBinding>(R.layout.activity_sign) {
    private val viewModel: SignViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
    }
}