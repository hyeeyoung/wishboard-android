package com.hyeeyoung.wishboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.hyeeyoung.wishboard.databinding.ActivitySplashBinding
import com.hyeeyoung.wishboard.view.sign.SignActivity
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        lifecycleScope.launch(Dispatchers.Main) {
            job = launch {
                delay(2000)
                startActivity(Intent(this@SplashActivity, SignActivity::class.java))
                finish()
            }
        }
    }

    override fun onPause() {
        job?.cancel()
        super.onPause()
    }
}