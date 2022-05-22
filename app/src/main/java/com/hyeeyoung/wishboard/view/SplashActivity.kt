package com.hyeeyoung.wishboard.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.WishBoardApp
import com.hyeeyoung.wishboard.databinding.ActivitySplashBinding
import com.hyeeyoung.wishboard.view.sign.screens.SignActivity
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
        // TODO 유저 정보 가져오기
        val token = WishBoardApp.prefs.getUserToken()
        Log.d(TAG, "token : $token")
        if (token == null) {
            startActivity(Intent(this@SplashActivity, SignActivity::class.java))
        } else {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }
    }

    override fun onPause() {
        job?.cancel()
        super.onPause()
    }

    companion object {
        private const val TAG = "SplashActivity"
    }
}