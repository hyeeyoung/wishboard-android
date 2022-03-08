package com.hyeeyoung.wishboard.view.sign.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.ActivitySignBinding
import com.hyeeyoung.wishboard.util.NetworkConnection
import com.hyeeyoung.wishboard.viewmodel.SignViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignBinding
    private val viewModel: SignViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign)
        binding.viewModel = viewModel

        addObservers()
    }

    private fun addObservers() {
        NetworkConnection(this).observe(this) { isConnected ->
            if (isConnected == false) {
                Toast.makeText(
                    this,
                    getString(R.string.check_network_connection_toast_text),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}