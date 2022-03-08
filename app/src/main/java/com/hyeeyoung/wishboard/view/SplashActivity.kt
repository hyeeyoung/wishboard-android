package com.hyeeyoung.wishboard.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.ActivitySplashBinding
import com.hyeeyoung.wishboard.util.PreferenceUtil
import com.hyeeyoung.wishboard.util.prefs
import com.hyeeyoung.wishboard.view.sign.screens.SignActivity
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        prefs = PreferenceUtil(applicationContext)

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
        val token = prefs?.getUserToken()
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