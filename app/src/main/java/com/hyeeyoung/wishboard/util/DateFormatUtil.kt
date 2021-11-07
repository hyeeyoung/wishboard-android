package com.hyeeyoung.wishboard.util

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateFormatUtil {
    /** 시간 정보 포맷을 지정 */
    fun beforeTime(strDate: String): String? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date: Date?
        try {
            date = dateFormat.parse(strDate)
        } catch (e: ParseException) {
            e.printStackTrace()
            return null
        }
        val c = Calendar.getInstance()
        val now = c.timeInMillis
        val before = date!!.time
        var gap = (now - before) / 1000
        var timeInfo: String?

        if (gap < TIME_MAXIMUM.SEC) {
            timeInfo = "방금 전"
        } else if (TIME_MAXIMUM.SEC.let { gap /= it; gap } < TIME_MAXIMUM.MIN) {
            timeInfo = gap.toString() + "분 전"
        } else if (TIME_MAXIMUM.MIN.let { gap /= it; gap } < TIME_MAXIMUM.HOUR) {
            timeInfo = gap.toString() + "시간 전"
        } else if (TIME_MAXIMUM.HOUR.let { gap /= it; gap } < TIME_MAXIMUM.WEEK) {
            timeInfo = gap.toString() + "일 전"
        } else {
            gap /= TIME_MAXIMUM.WEEK.toLong()
            timeInfo = gap.toString() + "주 전"
        }
        return timeInfo
    }

    /** 날짜를 "yy.M.d" 포맷으로 변경 */
    fun shortDateYMD(str_date: String?): String? {
        val date_format1: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date_format2: DateFormat = SimpleDateFormat("yy.M.d")
        var date1: Date?
        var date2: String
        try {
            date1 = date_format1.parse(str_date)
            date2 = date_format2.format(date1)
        } catch (e: ParseException) {
            e.printStackTrace()
            return null
        }
        return date2
    }

    /** 날짜를 "M월 d일 HH시 m분" 포맷으로 변경 */
    fun shortDateMDHM(str_date: String?): String? {
        val date_format1: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val date_format2: DateFormat = SimpleDateFormat("M월 d일 HH시 m분")
        var date1: Date?
        var date2: String
        try {
            date1 = date_format1.parse(str_date)
            date2 = date_format2.format(date1)
        } catch (e: ParseException) {
            e.printStackTrace()
            return null
        }
        if (date2.substring(date2.length - 3) == " 0분") {
            date2 = date2.replace("0분", "")
        }
        return date2
    }

    /** D-day 계산 */
    fun countDday(year: Int, month: Int, day: Int): String {
        return try {
            val todayCal = Calendar.getInstance()
            val ddayCal = Calendar.getInstance()
            ddayCal[year, month - 1] = day
            val today =
                todayCal.timeInMillis / 86400000 // (24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
            val dday = ddayCal.timeInMillis / 86400000
            val count = (dday - today).toInt()
            if (count > 0) "-$count" else if (count < 0) "None" else "-Day"
        } catch (e: Exception) {
            e.printStackTrace()
            "None"
        }
    }

    /** 시간 단위 별 최댓값 */
    private object TIME_MAXIMUM {
        const val SEC = 60
        const val MIN = 60
        const val HOUR = 24
        const val WEEK = 7
    }
}