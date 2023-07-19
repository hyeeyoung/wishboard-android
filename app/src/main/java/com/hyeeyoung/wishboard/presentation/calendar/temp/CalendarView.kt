package com.hyeeyoung.wishboard.presentation.calendar.temp

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.util.CalendarUtils.Companion.WEEKS_PER_MONTH
import com.hyeeyoung.wishboard.presentation.noti.NotiViewModel
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants.DAYS_PER_WEEK
import kotlin.math.max

class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.calendarViewStyle,
    @StyleRes defStyleRes: Int = R.style.Calendar_CalendarViewStyle
) : ViewGroup(ContextThemeWrapper(context, defStyleRes), attrs, defStyleAttr) {
    private var _height: Float = 0f
    private val dayViews: MutableList<DayItemView> = mutableListOf()
//    private var selectedDate: DayItemView? = null

    init {
        context.withStyledAttributes(attrs, R.styleable.CalendarView, defStyleAttr, defStyleRes) {
            _height = getDimension(R.styleable.CalendarView_dayHeight, 0f)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val h = paddingTop + paddingBottom + max(
            suggestedMinimumHeight,
            (_height * WEEKS_PER_MONTH).toInt()
        )
        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec), h)
    }

    /**
     * children : 월별 날짜 뷰 (총 42개)
     * */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val itemWidth = (width / DAYS_PER_WEEK).toFloat()
        val itemHeight = (height / WEEKS_PER_MONTH).toFloat()

        var index = 0
        children.forEach { view ->
            val left = (index % DAYS_PER_WEEK) * itemWidth
            val top = (index / DAYS_PER_WEEK) * itemHeight
            view.layout(
                left.toInt(),
                top.toInt(),
                (left + itemWidth).toInt(),
                (top + itemHeight).toInt()
            )

            index++
        }
    }

    /**
     * 달력 그리기 시작
     * @param firstDayOfMonth : 한 달의 시작 요일
     * @param list : 월별 날짜와 이벤트 목록 (총 42개)
     */
    fun initCalendar(firstDayOfMonth: DateTime, list: List<DateTime>, viewModel: NotiViewModel) {
        list.forEach { date ->
            val view = DayItemView(
                context = context,
                date = date,
                firstDayOfMonth = firstDayOfMonth,
            )
            view.setOnClickListener {
                viewModel.setSelectedNotiList(date.toString("yyyy-MM-dd"))
                // TODO 선택된 날짜 글씨 두껍게 하기
//                selectedDate?.let { view.setSelectedView(false) }
//                selectedDate = view
//                view.setSelectedView(true)
            }
            dayViews.add(view)
            addView(
                view
            )
        }
    }

    fun showNotiMark(notiDateList: List<String>) {
        dayViews.forEach {
            if (notiDateList.contains(it.getDate())) {
                it.setExistNoti(true)
            }
        }
    }
}