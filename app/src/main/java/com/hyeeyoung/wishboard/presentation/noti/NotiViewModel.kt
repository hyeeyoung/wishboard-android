package com.hyeeyoung.wishboard.presentation.noti

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.WishBoardApp
import com.hyeeyoung.wishboard.domain.model.NotiItemInfo
import com.hyeeyoung.wishboard.domain.repositories.NotiRepository
import com.hyeeyoung.wishboard.presentation.noti.adapters.NotiListAdapter
import com.hyeeyoung.wishboard.presentation.noti.types.NotiListViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NotiViewModel @Inject constructor(
    private val notiRepository: NotiRepository,
) : ViewModel() {
    private val token = WishBoardApp.prefs.getUserToken()

    private var notiList = MutableLiveData<List<NotiItemInfo>?>(listOf())
    private var selectedNotiList = MutableLiveData<List<NotiItemInfo>?>(listOf())
    private var notiDateList = MutableLiveData<List<String>?>(listOf())
    private val notiListAdapter = NotiListAdapter(NotiListViewType.NOTI_TAB_VIEW_TYPE)
    private val calendarNotiListAdapter = NotiListAdapter(NotiListViewType.CALENDAR_VIEW_TYPE)
    private val calendarMonthTitle = MutableLiveData<String>()
    private val selectedDate = MutableLiveData<String>()

    init {
        fetchAllNotiList()
    }

    fun fetchPreviousNotiList() {
        if (token == null) return
        viewModelScope.launch {
            val items = notiRepository.fetchPreviousNotiList(token)?.map { it.toNotiItemInfo() }
            notiList.value = items
            notiListAdapter.setData(items)
        }
    }

    fun fetchAllNotiList() {
        if (token == null) return
        viewModelScope.launch {
            val items = notiRepository.fetchAllNotiList(token)?.map { it.toNotiItemInfo() }
            notiList.value = items
            setNotiDateList(items) // 캘린더 뷰 알림 날짜 표시를 위한 notiDateList 만들기
        }
    }

    fun updateNotiReadState(position: Int, itemId: Long) {
        if (token == null) return
        notiListAdapter.updateReadState(position)
        viewModelScope.launch {
            notiRepository.updateNotiReadState(token, itemId)
        }
    }

    fun setSelectedNotiList(targetDate: String) {
        selectedDate.value = targetDate
        val items = notiList.value?.filter {
            targetDate == it.notiDate.substring(0, 10)
        }
        selectedNotiList.value = items
        calendarNotiListAdapter.setData(items)
    }

    private fun setNotiDateList(notiList: List<NotiItemInfo>?) {
        notiDateList.value = notiList?.map {
            it.notiDate.substring(0, 10)
        }
    }

    /** 캘린더 년도 및 월 타이틀 초기화 */
    fun setCalendarMonthTitle(millis: Long) {
        calendarMonthTitle.value = DateTime(millis).toString("MMMM yyyy", Locale("en"))
    }

    fun getNotiList(): LiveData<List<NotiItemInfo>?> = notiList
    fun getSelectedNotiList(): LiveData<List<NotiItemInfo>?> = selectedNotiList
    fun getNotiDateList(): LiveData<List<String>?> = notiDateList
    fun getNotiListAdapter(): NotiListAdapter = notiListAdapter
    fun getCalendarNotiListAdapter(): NotiListAdapter = calendarNotiListAdapter
    fun getCalendarMonthTitle(): LiveData<String> = calendarMonthTitle
    fun getSelectedDate(): LiveData<String> = selectedDate
}