package com.hyeeyoung.wishboard.presentation.calendar.temp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.util.CalendarUtils.Companion.isSameDate
import com.hyeeyoung.wishboard.util.CalendarUtils.Companion.isSameMonth
import org.joda.time.DateTime

class DayItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes private val defStyleAttr: Int = R.attr.itemViewStyle,
    @StyleRes private val defStyleRes: Int = R.style.Calendar_ItemViewStyle,
    private val date: DateTime = DateTime(),
    private val firstDayOfMonth: DateTime = DateTime(),
) : View(ContextThemeWrapper(context, defStyleRes), attrs, defStyleAttr) {
    private val bounds = Rect()
    private var paint: Paint = Paint()
    private var isExistNoti: Boolean = false
//    private var isSelected: Boolean? = null

    init {
        // Attributes
        context.withStyledAttributes(attrs, R.styleable.CalendarView, defStyleAttr, defStyleRes) {
            val dayTextSize = getDimensionPixelSize(R.styleable.CalendarView_dayTextSize, 0).toFloat()

            // 날짜뷰 텍스트 속성 설정
            paint = TextPaint().apply {
                isAntiAlias = true
                textSize = dayTextSize
                color = ResourcesCompat.getColor(resources, R.color.gray_700, null)
                paint.typeface = ResourcesCompat.getFont(context, R.font.montserrat_r)

                // 이전 및 다음달 날짜 미리보기
                if (!isSameMonth(date, firstDayOfMonth)) {
                    alpha = 50
                }

                // 오늘 날짜 표기
                if (isSameDate(date)) {
                    background = ResourcesCompat.getDrawable(resources, R.drawable.shape_circle, null)
                    backgroundTintList = ContextCompat.getColorStateList(this@DayItemView.context, R.color.green_500)
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        val date = date.dayOfMonth.toString()
        paint.getTextBounds(date, 0, date.length, bounds)
        canvas.drawText(
            date,
            (width / 2 - bounds.width() / 2).toFloat() - 2,
            (height / 2 + bounds.height() / 2).toFloat(),
            paint
        )

        if (!isSameDate(this.date) && isExistNoti) {
            background = ResourcesCompat.getDrawable(resources, R.drawable.shape_circle, null)
            backgroundTintList = ContextCompat.getColorStateList(this@DayItemView.context, R.color.green_200)
        }
    }

    fun getDate(): String {
        return date.toString("yyyy-MM-dd")
    }

    fun setExistNoti(isExist: Boolean) {
        isExistNoti = isExist
        invalidate()
    }

    // TODO 선택된 날짜 글씨 두껍게 처리
//    fun setSelectedView(isSelected: Boolean) {
//        this.isSelected = isSelected
//        invalidate()
//    }
}