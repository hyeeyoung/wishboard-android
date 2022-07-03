package com.hyeeyoung.wishboard.util

import android.widget.NumberPicker
import com.hyeeyoung.wishboard.presentation.noti.types.NotiType
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.collections.ArrayList

private var notiTypes = NotiType.values().toList()

/** 현재부터 90일 후까지의 날짜 배열(MMM dd일 EEE) 생성 */
private val dates: Array<String>
    get() {
        val c = Calendar.getInstance()
        val dates: MutableList<String> = ArrayList()
        val dateFormat: DateFormat =
            SimpleDateFormat("MMM dd일 EEE")

        dates.add(dateFormat.format(c.time))
        for (i in 0..89) {
            c.add(Calendar.DATE, 1)
            dates.add(dateFormat.format(c.time))
        }
        return dates.toTypedArray()
    }

/** 서버에 저장될 날짜 배열(yyyy-mm-dd hh:mm:ss) 생성 */
private val datesForServer: Array<String>
    get() {
        val c = Calendar.getInstance()
        val datesServer: MutableList<String> = ArrayList()
        val dateFormatServer: DateFormat =
            SimpleDateFormat("yyyy-MM-dd")

        datesServer.add(dateFormatServer.format(c.time))
        for (i in 0..89) {
            c.add(Calendar.DATE, 1)
            datesServer.add(dateFormatServer.format(c.time))
        }
        return datesServer.toTypedArray()
    }

private val hours: Array<String>
    get() {
        val now = Calendar.getInstance()
        val currentHour = (now[Calendar.HOUR_OF_DAY] + 1) % 24 // TODO 다음날로 바꾸기 및 테스트 해보기
        return ((currentHour..23).toList() + (0 until currentHour).toList()).map { it.toString() }
            .toTypedArray()
    }

private val minutes: Array<String>
    get() {
        val minutes: MutableList<String> = ArrayList()
        var i = 0
        while (i < 60) {
            minutes.add(String.format("%02d", i))
            i += TIME_PICKER_INTERVAL
        }
        return minutes.toTypedArray()
    }

/** 30분 단위의 알림 시간 지정 */
const val TIME_PICKER_INTERVAL = 30

fun setTypePicker(typePicker: NumberPicker) {
    typePicker.apply {
        value = 0
        maxValue = notiTypes.size - 1
        displayedValues =
            notiTypes.map { this.context.resources.getString(it.strRes) }.toTypedArray()
        descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }
}

fun setDatePicker(datePicker: NumberPicker) {
    datePicker.apply {
        value = 0
        maxValue = dates.size - 1
        setFormatter { value -> dates[value] }
        displayedValues = dates
        descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }
}

fun setHourPicker(hourPicker: NumberPicker) {
    hourPicker.apply {
        value = 0
        maxValue = hours.size - 1
        displayedValues = hours
        wrapSelectorWheel = true
        descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }
}

fun setMinutePicker(minutePicker: NumberPicker) {
    minutePicker.apply {
        value = 0
        maxValue = minutes.size - 1
        displayedValues = minutes
        wrapSelectorWheel = true
        descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }
}

fun getTypePickerValue(index: Int?): NotiType? {
    if (index == null) return null
    return notiTypes[index]
}

fun getDatePickerValue(dateIndex: Int?, hourIndex: Int?, minuteIndex: Int?): String {
    return getNotiDateServerFormat(
        datesForServer[dateIndex ?: 0],
        hours[hourIndex ?: 0],
        minutes[minuteIndex ?: 0]
    )
}