package com.hyeeyoung.wishboard.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun convertStrDateToLocalDate(strDate: String?): LocalDateTime {
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return LocalDateTime.parse(strDate, dateFormat)
}

/** 시간 포맷 지정 */
fun convertStrTimeToDate(strDate: String?): Long? {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date: Date?
    try {
        date = dateFormat.parse(strDate ?: return null)
    } catch (e: ParseException) {
        e.printStackTrace()
        return null
    }
    return date.time
}

/** 날짜를 "yy년 M월 d일" 포맷으로 변경 */
fun convertDateToYMD(date: Date): String? {
    val dateFormat = SimpleDateFormat("yy년 M월 d일")
    return try {
        dateFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }
}

/** 날짜를 "yy년 M월 d일" 포맷으로 변경 */
fun convertYMDHMToMD(date: String?): String? {
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")
    val outputDateFormat = SimpleDateFormat("M월 d일")
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

/** 날짜를 "yy. M. d a h:mm" 포맷으로 변경 */
fun convertYMDHMToYMDAHM(date: String?): String? {
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
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
fun calculateDday(date: String): String? {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val endDate: Date
    try {
        endDate = dateFormat.parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
        return null
    }

    val today = getIgnoredTimeDays(Calendar.getInstance().time.time)
    val dday = (today - getIgnoredTimeDays(endDate.time)) / (24 * 60 * 60 * 1000)

    return when {
        dday < 0L -> { // 디데이 경과 전 -> ex) "D-6"
            "D-${dday}"
        }
        dday > 0L -> { // 디데이 경과 후 -> ex) "21년 1월 1일"
            convertDateToYMD(endDate)
        }
        else -> { // 디데이 당일 -> ex) "오늘 15시 30분"
            "오늘 ${convertYMDHMToHourMinute(endDate)}"
        }
    }
}

/** "h시 m분" 포맷으로 변환 */
fun convertYMDHMToHourMinute(date: Date): String {
    val dateFormat = SimpleDateFormat("H:m")
    val hourMinute: String
    try {
        hourMinute = dateFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
        return ""
    }

    val dividerIdx = hourMinute.indexOf(":")
    val hour = hourMinute.substring(0, dividerIdx)
    val minute = hourMinute.substring(dividerIdx + 1)

    return if (minute[0] == '0') {
        "${hour}시"
    } else {
        "${hour}시 ${minute}분"
    }
}

/** 날짜를 "a h시 mm분" 포맷으로 변경 */
fun convertYMDHMToAHM(date: String?): String {
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val outputDateFormat = SimpleDateFormat("a h시 m분")
    val inputDate: Date?
    val outputDate: String
    try {
        inputDate = inputDateFormat.parse(date)
        outputDate = outputDateFormat.format(inputDate)
    } catch (e: ParseException) {
        e.printStackTrace()
        return ""
    }

    val hourIndex = outputDate.indexOf("시")
    return if (outputDate.endsWith("0분")) {
        outputDate.substring(0, hourIndex + 1)
    } else {
        outputDate
    }
}

/** 날짜를 "yy년 M월 d일 H시 m분" 포맷으로 변경 */
fun convertKoreanDate(date: String?): String? {
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val inputDate: Date?
    try {
        inputDate = inputDateFormat.parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
        return null
    }

    return convertDateToYMD(inputDate) + " " + convertYMDHMToHourMinute(inputDate)
}

/** 시간, 분, 초 제거 */
fun getIgnoredTimeDays(time: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = time
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}