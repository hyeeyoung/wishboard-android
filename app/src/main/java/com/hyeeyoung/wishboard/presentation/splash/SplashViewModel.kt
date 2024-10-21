package com.hyeeyoung.wishboard.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.data.local.WishBoardPreference
import com.hyeeyoung.wishboard.domain.repositories.SystemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val localStorage: WishBoardPreference,
    private val systemRepository: SystemRepository,
) : ViewModel() {
    fun isLogin() = localStorage.isLogin

    fun checkForAppUpdate(
        playStoreVersionCode: Int,
        moveToNext: () -> Unit,
        showUpdateDialog: (isForceUpdate: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            systemRepository.fetchAppVersion().onSuccess { remoteVersion ->
                if (remoteVersion == null) {
                    moveToNext()
                    return@launch
                }

                when {
                    BuildConfig.VERSION_CODE < playStoreVersionCode -> {
                        showUpdateDialog(BuildConfig.VERSION_CODE < remoteVersion.minVersionCode)
                    }

                    else -> {
                        moveToNext()
                    }
                }
            }.onFailure {
                moveToNext()
            }
        }
    }
}
