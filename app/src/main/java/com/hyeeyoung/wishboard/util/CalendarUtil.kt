package com.hyeeyoung.wishboard.util

import org.joda.time.DateTime
import org.joda.time.DateTimeConstants

class CalendarUtils {

    companion object {
        const val WEEKS_PER_MONTH = 6

        /** 선택된 날짜에 해당하는 월간 달력을 반환 */
        fun getMonthList(dateTime: DateTime): List<DateTime> {
            val list = mutableListOf<DateTime>()

            val date = dateTime.withDayOfMonth(1)
            val prev = getPrevOffSet(date)
            val startValue = date.minusDays(prev)
            val totalDay = DateTimeConstants.DAYS_PER_WEEK * WEEKS_PER_MONTH

            for (i in 0 until totalDay) {
                list.add(DateTime(startValue.plusDays(i)))
            }

            return list
        }

        /** 해당 calendar의 이전 달의 일 갯수를 반환 */
        private fun getPrevOffSet(dateTime: DateTime): Int {
            var prevMonthTailOffset = dateTime.dayOfWeek
            if (prevMonthTailOffset >= 7) {
                prevMonthTailOffset %= 7
            }

            return prevMonthTailOffset
        }

        /** 동일한 달인지 체크 */
        fun isSameMonth(first: DateTime, second: DateTime): Boolean =
            first.year == second.year && first.monthOfYear == second.monthOfYear
    }
}