package com.hyeeyoung.wishboard.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.hyeeyoung.wishboard.WishBoardApp
import com.hyeeyoung.wishboard.domain.repositories.NotiRepository
import com.hyeeyoung.wishboard.domain.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val notiRepository: NotiRepository,
) : ViewModel() {
    private val userToken = WishBoardApp.prefs.getUserToken()
    private val fcmToken = WishBoardApp.prefs.getFCMToken()

    fun initFCMToken() {
        if (userToken == null) return
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.w("Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val fcmToken = task.result
            Timber.d(fcmToken)

            if (this.fcmToken != fcmToken) {
                WishBoardApp.prefs.setFCMToken(fcmToken)
                viewModelScope.launch(Dispatchers.IO) {
                    userRepository.registerFCMToken(userToken, fcmToken)
                }
            }
        })
    }

    fun updatePushState() {
        if (userToken == null) return
        viewModelScope.launch {
            notiRepository.updatePushState(userToken, true)
        }
    }
}