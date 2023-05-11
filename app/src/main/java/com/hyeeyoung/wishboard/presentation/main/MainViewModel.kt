package com.hyeeyoung.wishboard.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.domain.repositories.NotiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val notiRepository: NotiRepository
) : ViewModel() {

    fun updatePushState() {
        viewModelScope.launch {
            notiRepository.updatePushState(true)
        }
    }
}