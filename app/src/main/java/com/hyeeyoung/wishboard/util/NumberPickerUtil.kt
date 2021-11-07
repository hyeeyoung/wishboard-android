package com.hyeeyoung.wishboard.util

import android.content.Context
import android.widget.NumberPicker
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NumberPickerUtil {
    /** 알림 유형 */
    private lateinit var notiTypesArray: Array<String>
    /** 팝업창에 디스플레이할 날짜 배열로, 포맷은 MMM dd일 EEE로 설정함 */
    private var dates: Array<String>
    /** DB에 저장될 날짜 배열로 datetime 타입 포맷인 yyyy-mm-dd hh:mm:ss로 설정함 */
    private var datesServer: Array<String>
    private var now = Calendar.getInstance()

    init {
        dates = datesFromCalender
        datesServer = datesFromCalenderForServer
    }

    /** : 알림 날짜 넘버피커 초기설정 */
    fun initNumberPicker(context: Context) {
        var type = NumberPicker(context)
        var date = NumberPicker(context)
        var hour = NumberPicker(context)
        var minute = NumberPicker(context)

        type.value = 0
        type.maxValue = notiTypesArray.size - 1
        type.displayedValues = notiTypesArray
        type.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        date.minValue = 0
        date.maxValue = dates.size - 1
        date.setFormatter { value -> dates[value] }
        date.displayedValues = dates
        date.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        hour.minValue = 0
        hour.maxValue = 23
        // TODO 다음날로 바꾸기 및 테스트 해보기
        hour.value =
            (now[Calendar.HOUR_OF_DAY] + 1) % 24
        hour.wrapSelectorWheel = true
        hour.descendantFocusability =
            NumberPicker.FOCUS_BLOCK_DESCENDANTS

        minute.minValue = 0
        minute.maxValue = 60 / TIME_PICKER_INTERVAL - 1
        val minutes: MutableList<String> = ArrayList()
        var i = 0
        while (i < 60) {
            minutes.add(String.format("%02d", i))
            i += TIME_PICKER_INTERVAL
        }
        minute.displayedValues = minutes.toTypedArray()
        minute.wrapSelectorWheel = true
        minute.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
    }

    /** 현재부터 90일 후까지의 날짜 배열 생성 */
    private val datesFromCalender: Array<String>
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

    /** 서버에 저장될 날짜 배열 반환 */
    private val datesFromCalenderForServer: Array<String>
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