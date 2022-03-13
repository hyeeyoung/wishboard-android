package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.noti.NotiItem
import com.hyeeyoung.wishboard.service.AWSS3Service
import com.hyeeyoung.wishboard.repository.noti.NotiRepository
import com.hyeeyoung.wishboard.util.prefs
import com.hyeeyoung.wishboard.view.noti.adapters.NotiListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NotiViewModel @Inject constructor(
    private val notiRepository: NotiRepository,
) : ViewModel() {
    private val token = prefs?.getUserToken()

    private var notiList = MutableLiveData<List<NotiItem>?>(listOf())
    private val notiListAdapter = NotiListAdapter()

    fun fetchNotiList() {
        if (token == null) return
        viewModelScope.launch {
            var items: List<NotiItem>?
            withContext(Dispatchers.IO) {
                items = notiRepository.fetchNotiList(token)
                items?.forEach { item ->
                    item.itemImg?.let { item.itemImageUrl = AWSS3Service().getImageUrl(it) }
                }
            }
            withContext(Dispatchers.Main) {
                notiList.postValue(items)
                notiListAdapter.setData(items)
            }
        }
    }

    fun updateNotiReadState(position: Int, itemId: Long) {
        if (token == null) return
        notiListAdapter.updateReadState(position)
        viewModelScope.launch {
            notiRepository.updateNotiReadState(token, itemId)
        }
    }

    fun getNotiList(): LiveData<List<NotiItem>?> = notiList
    fun getNotiListAdapter(): NotiListAdapter = notiListAdapter

    companion object {
        private const val TAG = "NotiViewModel"
    }
}