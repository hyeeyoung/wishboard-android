package com.hyeeyoung.wishboard.presentation.main

import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.domain.repositories.NotiRepository
import com.hyeeyoung.wishboard.presentation.base.viewmodel.NetworkViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val notiRepository: NotiRepository
) : NetworkViewModel() {

    fun updatePushState() {
        viewModelScope.launch {
            notiRepository.updatePushState(true)
        }
    }
}