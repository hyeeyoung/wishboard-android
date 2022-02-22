package com.hyeeyoung.wishboard.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/** 시간 단위 별 최댓값 */
const val SEC = 60
const val MIN = 60
const val HOUR = 24
const val WEEK = 7

/** 시간 정보 포맷을 지정 */
fun beforeTime(strDate: String): String? {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date: Date?
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
    val timeInfo: String?

    when {
        gap < SEC -> {
            timeInfo = "방금 전"
        }
        SEC.let { gap /= SEC; gap } < MIN -> {
            timeInfo = gap.toString() + "분 전"
        }
        MIN.let { gap /= it; gap } < HOUR -> {
            timeInfo = gap.toString() + "시간 전"
        }
        HOUR.let { gap /= it; gap } < WEEK -> {
            timeInfo = gap.toString() + "일 전"
        }
        else -> {
            gap /= WEEK.toLong()
            timeInfo = gap.toString() + "주 전"
        }
    }
    return timeInfo
}

/** 날짜를 "yy.M.d" 포맷으로 변경 */
fun shortDateYMD(str_date: String?): String? {
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")
    val outputDateFormat = SimpleDateFormat("yy.M.d")
    val date1: Date?
    val date2: String
    try {
        date1 = inputDateFormat.parse(str_date)
        date2 = outputDateFormat.format(date1)
    } catch (e: ParseException) {
        e.printStackTrace()
        return null
    }
    return date2
}

/** 날짜를 "M월 d일 HH시 m분" 포맷으로 변경 */
fun shortDateMDHM(str_date: String?): String? {
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val outputDateFormat = SimpleDateFormat("M월 d일 HH시 m분")
    val inputDate: Date?
    var outputDate: String
    try {
        inputDate = inputDateFormat.parse(str_date)
        outputDate = outputDateFormat.format(inputDate)
    } catch (e: ParseException) {
        e.printStackTrace()
        return null
    }
    if (outputDate.substring(outputDate.length - 3) == " 0분") {
        outputDate = outputDate.replace("0분", "")
    }
    return outputDate
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