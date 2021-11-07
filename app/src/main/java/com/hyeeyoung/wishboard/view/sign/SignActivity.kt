package com.hyeeyoung.wishboard.view.sign

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.ActivitySignBinding
import com.hyeeyoung.wishboard.viewmodel.SignViewModel

class SignActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignBinding
    private val viewModel: SignViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign)
        binding.viewModel = viewModel
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignActivity().apply {

            }
    }
}