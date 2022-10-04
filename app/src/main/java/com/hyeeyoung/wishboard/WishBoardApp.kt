package com.hyeeyoung.wishboard

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import com.hyeeyoung.wishboard.util.PreferenceUtil
import com.hyeeyoung.wishboard.util.WishBoardDebugTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class WishBoardApp : Application() {
    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceUtil(applicationContext)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setUpTimber()
        setUpAmplify()
    }

    private fun setUpAmplify() {
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(applicationContext)
            Timber.d("Initialized Amplify")
        } catch (error: AmplifyException) {
            Timber.e("Could not initialize Amplify", error)
        }
    }

    private fun setUpTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(WishBoardDebugTree())
        }
    }

    companion object {
        lateinit var prefs: PreferenceUtil
    }
}