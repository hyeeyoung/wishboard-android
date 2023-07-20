package com.hyeeyoung.wishboard.presentation.splash

import androidx.lifecycle.ViewModel
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val localStorage: WishBoardPreference,
) : ViewModel() {
    fun isLogin() = localStorage.isLogin
}
