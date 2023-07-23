package com.hyeeyoung.wishboard

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.hyeeyoung.wishboard.util.WishBoardDebugTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class WishBoardApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setUpTimber()
    }

    private fun setUpTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(WishBoardDebugTree())
        }
    }
}