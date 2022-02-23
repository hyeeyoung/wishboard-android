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

/** 날짜를 "yy년 M월 d일" 포맷으로 변경 */
fun shortDateYMD(date: Date): String? {
    val dateFormat = SimpleDateFormat("yy년 M월 d일")
    return try {
        dateFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }
}

/** 날짜를 "yy. M. d a h:mm" 포맷으로 변경 */
fun shortDateYMDAHM(date: String?): String? {
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val outputDateFormat = SimpleDateFormat("yy. M. d a h:mm")
    val inputDate: Date?
    val outputDate: String
    try {
        inputDate = inputDateFormat.parse(date)
        outputDate = outputDateFormat.format(inputDate)
    } catch (e: ParseException) {
        e.printStackTrace()
        return null
    }
    return outputDate
}

/** D-day 계산 */
fun countDday(date: String): String? {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val endDate: Date
    try {
        endDate = dateFormat.parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
        return null
    }
    val today = Calendar.getInstance().time
    val dday = (endDate.time - today.time) / (24 * 60 * 60 * 1000)

    return when {
        dday > 0L -> { // 디데이 경과 전 -> ex) "D-6"
            "D-${dday}"
        }
        dday < 0L -> { // 디데이 경과 후 -> ex) "21년 1월 1일"
            shortDateYMD(endDate)
        }
        else -> { // 디데이 당일 -> ex) "오늘 15시 30분"
            val minutes = date.substring(14,16)

            if (minutes == "00") { // 00분인 경우 분 표시 안함
                "오늘 ${date.substring(11,13)}시"
            } else {
                "오늘 ${date.substring(11,13)}시 ${date.substring(14,16)}분"
            }
        }
    }
}
