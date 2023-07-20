package com.hyeeyoung.wishboard.presentation.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.ActivitySplashBinding
import com.hyeeyoung.wishboard.presentation.common.screens.TwoButtonDialogFragment
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.presentation.main.MainActivity
import com.hyeeyoung.wishboard.presentation.sign.screens.SignActivity
import com.hyeeyoung.wishboard.util.BaseActivity
import com.hyeeyoung.wishboard.util.DialogListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch() {
            delay(2000)
            checkForNewVersionUpdate()
        }
    }

    private fun moveToNext() {
        val isLogin = viewModel.isLogin()
        val nextScreen = if (isLogin) MainActivity::class.java else SignActivity::class.java
        startActivity(Intent(this@SplashActivity, nextScreen))
        finish()
    }

    private fun checkForNewVersionUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this@SplashActivity)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                && appUpdateInfo.availableVersionCode() != BuildConfig.VERSION_CODE
            ) {
                showUpdateDialog()
            } else {
                moveToNext()
            }
        }.addOnFailureListener {
            moveToNext()
        }
    }

    private fun showUpdateDialog() {
        TwoButtonDialogFragment(
            getString(R.string.app_update_dialog_title),
            getString(R.string.app_update_dialog_description),
            getString(R.string.app_update_dialog_yes_button_text),
            getString(R.string.later)
        ).apply {
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: String) {
                    if (clicked == DialogButtonReplyType.YES.name) moveToPlayStore()
                    else moveToNext()
                }
            })
        }.show(supportFragmentManager, "UpdateDialog")
    }

    private fun moveToPlayStore() {
        Intent(Intent.ACTION_VIEW).apply {
            data =
                Uri.parse("${getString(R.string.play_store_detail_url)}${this@SplashActivity.packageName}")
        }.also {
            startActivity(it)
        }
    }
}
