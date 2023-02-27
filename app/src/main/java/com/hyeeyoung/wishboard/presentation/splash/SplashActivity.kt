package com.hyeeyoung.wishboard.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.databinding.ActivitySplashBinding
import com.hyeeyoung.wishboard.presentation.main.MainActivity
import com.hyeeyoung.wishboard.presentation.sign.screens.SignActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        lifecycleScope.launch(Dispatchers.Main) {
            job = launch {
                delay(2000)
                moveToNext()
                finish()
            }
        }
    }

    private fun moveToNext() {
        val isLogin = WishBoardPreference(this).isLogin
        val nextScreen = if (isLogin) MainActivity::class.java else SignActivity::class.java
        startActivity(Intent(this@SplashActivity, nextScreen))
    }

    override fun onPause() {
        job?.cancel()
        super.onPause()
    }
}