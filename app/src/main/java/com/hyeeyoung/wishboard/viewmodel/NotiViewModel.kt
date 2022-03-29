package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.noti.NotiItem
import com.hyeeyoung.wishboard.repository.noti.NotiRepository
import com.hyeeyoung.wishboard.service.AWSS3Service
import com.hyeeyoung.wishboard.util.prefs
import com.hyeeyoung.wishboard.view.noti.adapters.NotiListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NotiViewModel @Inject constructor(
    private val notiRepository: NotiRepository,
) : ViewModel() {
    private val token = prefs?.getUserToken()

    private var notiList = MutableLiveData<List<NotiItem>?>(listOf())
    private val notiListAdapter = NotiListAdapter()
    private val calendarMonthTitle = MutableLiveData<String>()


    fun fetchPreviousNotiList() {
        if (token == null) return
        viewModelScope.launch {
            var items: List<NotiItem>?
            withContext(Dispatchers.IO) {
                items = notiRepository.fetchPreviousNotiList(token)
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

    fun fetchAllNotiList() {
        if (token == null) return
        viewModelScope.launch {
            var items: List<NotiItem>?
            withContext(Dispatchers.IO) {
                items = notiRepository.fetchAllNotiList(token)
                items?.forEach { item ->
                    item.itemImg?.let { item.itemImageUrl = AWSS3Service().getImageUrl(it) }
                }
            }
            withContext(Dispatchers.Main) {
                notiList.postValue(items)
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

    /** 캘린더 년도 및 월 타이틀 초기화 */
    fun setCalendarMonthTitle(millis: Long) {
        calendarMonthTitle.value = DateTime(millis).toString("MMMM yyyy", Locale("en"))
    }

    fun getNotiList(): LiveData<List<NotiItem>?> = notiList
    fun getNotiListAdapter(): NotiListAdapter = notiListAdapter
    fun getCalendarMonthTitle(): LiveData<String> = calendarMonthTitle

    companion object {
        private const val TAG = "NotiViewModel"
    }
}