package com.hyeeyoung.wishboard.view.sign

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.ActivitySignBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignInActivity().apply {

            }
    }
}