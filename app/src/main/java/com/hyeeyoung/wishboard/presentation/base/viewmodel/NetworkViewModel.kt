package com.hyeeyoung.wishboard.presentation.base.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class NetworkViewModel :
    ViewModel() {
    private var _isConnected = MutableStateFlow(false)
    val isConnected get() = _isConnected.asStateFlow()

    fun setConnected(isConnected: Boolean) {
        _isConnected.value = isConnected
    }
}
