package com.hyeeyoung.wishboard.presentation.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.WishBoardApp
import com.hyeeyoung.wishboard.databinding.ActivitySplashBinding
import com.hyeeyoung.wishboard.presentation.common.screens.TwoButtonDialogFragment
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.presentation.main.MainActivity
import com.hyeeyoung.wishboard.presentation.sign.screens.SignActivity
import com.hyeeyoung.wishboard.util.DialogListener
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
                showServiceInterruptionDialog()
//                moveToNext()
//                finish()
            }
        }
    }

    private fun showServiceInterruptionDialog() {
        TwoButtonDialogFragment(
            "서비스 일시 중단 안내",
            "서버 이전으로 서비스가 \n" +
                    "일시 중단되오니 양해 부탁드립니다. \n" +
                    "보다 안정적인 위시보드로 곧 돌아올게요!\n" +
                    "자세한 사항은 공지사항을 확인해 주세요 \uD83D\uDE09",
            "앱 종료",
            "공지사항 확인"
        ).apply {
            isCancelable = false
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: String) {
                    if (clicked == DialogButtonReplyType.YES.name) {
                        dismiss()
                        finish()
                    } else {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/p/CtN6KfUPqbI/?igshid=NTc4MTIwNjQ2YQ==")))
                    }
                }
            })
        }.show(supportFragmentManager, "ServiceInterruptionDialog")
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