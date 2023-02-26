package com.hyeeyoung.wishboard.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
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
    private val localStorage: WishBoardPreference
) : ViewModel() {
    fun initFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.w("Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val fcmToken = task.result
            Timber.d(fcmToken)

            if (localStorage.fcmToken != fcmToken) {
                localStorage.fcmToken = fcmToken
                viewModelScope.launch(Dispatchers.IO) {
                    userRepository.registerFCMToken(fcmToken)
                }
            }
        })
    }

    fun updatePushState() {
        viewModelScope.launch {
            notiRepository.updatePushState(true)
        }
    }
}