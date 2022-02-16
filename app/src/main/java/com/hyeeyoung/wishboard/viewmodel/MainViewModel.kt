package com.hyeeyoung.wishboard.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.hyeeyoung.wishboard.repository.user.UserRepository
import com.hyeeyoung.wishboard.util.prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    fun initFCMToken() {
        val userToken = prefs?.getUserToken() ?: return
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val fcmToken = task.result
            Log.d(TAG, fcmToken)

            if (prefs?.getFCMToken() != fcmToken) {
                prefs?.setFCMToken(fcmToken)
                viewModelScope.launch(Dispatchers.IO) {
                    userRepository.registerFCMToken(userToken, fcmToken)
                }
            }
        })
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}