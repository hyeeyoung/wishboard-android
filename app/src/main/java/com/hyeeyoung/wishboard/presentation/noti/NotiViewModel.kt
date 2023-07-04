package com.hyeeyoung.wishboard.presentation.noti

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.domain.model.NotiItem
import com.hyeeyoung.wishboard.domain.model.NotiItemInfo
import com.hyeeyoung.wishboard.domain.repositories.NotiRepository
import com.hyeeyoung.wishboard.presentation.noti.types.NotiListViewType
import com.hyeeyoung.wishboard.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NotiViewModel @Inject constructor(
    private val notiRepository: NotiRepository,
) : ViewModel() {
    private val _calendarNotiList = MutableStateFlow<List<NotiItem>?>(listOf())
    val calendarNotiList get() = _calendarNotiList.asStateFlow()
    private val _notiFetchState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val notiFetchState get() = _notiFetchState.asStateFlow()
    private var notiList = MutableLiveData<List<NotiItemInfo>?>(listOf())
    private var selectedNotiList = MutableLiveData<List<NotiItemInfo>?>(listOf())
    private var notiDateList = MutableLiveData<List<String>?>(listOf())
    private val notiListAdapter = NotiListAdapter(NotiListViewType.NOTI_TAB_VIEW_TYPE)
    private val calendarNotiListAdapter = NotiListAdapter(NotiListViewType.CALENDAR_VIEW_TYPE)
    private val calendarMonthTitle = MutableLiveData<String>()
    private val selectedDate = MutableLiveData<String>()

    fun fetchPreviousNotiList() {
        viewModelScope.launch {
            val items = notiRepository.fetchPreviousNotiList()?.map { it.toNotiItemInfo() }
            _notiFetchState.value = if (items == null) UiState.Error(null) else UiState.Success(true)
            notiList.value = items
            notiListAdapter.setData(items)
        }
    }

    fun fetchAllNotiList() {
        viewModelScope.launch {
            val items = notiRepository.fetchAllNotiList()?.map { it.toNotiItemInfo() }
            _notiFetchState.value = if (items == null) UiState.Error(null) else UiState.Success(true)
            notiList.value = items
            setNotiDateList(items) // 캘린더 뷰 알림 날짜 표시를 위한 notiDateList 만들기
        }
    }

    fun fetchCalendarNotiList() {
        viewModelScope.launch {
            val items = notiRepository.fetchAllNotiList()?.map { it.toNotiItem() }
            _notiFetchState.value = if (items == null) UiState.Error(null) else UiState.Success(true)
            _calendarNotiList.value = items
        }
    }

    fun updateNotiReadState(position: Int, itemId: Long) {
        notiListAdapter.updateReadState(position)
        viewModelScope.launch {
            notiRepository.updateNotiReadState(itemId)
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