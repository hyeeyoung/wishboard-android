package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.repository.noti.NotiRepository
import com.hyeeyoung.wishboard.util.prefs
import com.hyeeyoung.wishboard.view.noti.adapters.NotiListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotiViewModel @Inject constructor(
    private val notiRepository: NotiRepository,
) : ViewModel() {
    private val token = prefs?.getUserToken()

    private val notiListAdapter = NotiListAdapter()

    fun fetchNotiList() {
        if (token == null) return
        viewModelScope.launch {
            notiListAdapter.setData(notiRepository.fetchNotiList(token) ?: return@launch)
        }
    }

    fun getNotiListAdapter(): NotiListAdapter = notiListAdapter

    companion object {
        private const val TAG = "NotiViewModel"
    }
}