package com.hyeeyoung.wishboard.util

import android.widget.NumberPicker
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NumberPickerUtil {
    private lateinit var notiTypes: Array<String>

    fun setNotiTypes(notiTypesArray: Array<String>) {
        this.notiTypes = notiTypesArray
     }

    fun setTypePicker(typePicker: NumberPicker) {
        typePicker.run {
            value = 0
            maxValue = notiTypes.size - 1
            displayedValues = notiTypes
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
    }

    fun setDatePicker(datePicker: NumberPicker) {
        datePicker.run {
            minValue = 0
            maxValue = dates.size - 1
            setFormatter { value -> dates[value] }
            displayedValues = dates
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
    }

    fun setHourPicker(hourPicker: NumberPicker) {
        val now = Calendar.getInstance()
        hourPicker.run {
            minValue = 0
            maxValue = 23
            value = (now[Calendar.HOUR_OF_DAY] + 1) % 24 // TODO 다음날로 바꾸기 및 테스트 해보기
            wrapSelectorWheel = true
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
    }

    fun setMinutePicker(minutePicker: NumberPicker) {
        minutePicker.run {
            minValue = 0
            maxValue = 60 / TIME_PICKER_INTERVAL - 1
            val minutes: MutableList<String> = ArrayList()
            var i = 0
            while (i < 60) {
                minutes.add(String.format("%02d", i))
                i += TIME_PICKER_INTERVAL
            }
            displayedValues = minutes.toTypedArray()
            wrapSelectorWheel = true
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
    }

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

    companion object {
        /** 5분 단위의 알림 시간 지정 */
        const val TIME_PICKER_INTERVAL = 5
    }
}