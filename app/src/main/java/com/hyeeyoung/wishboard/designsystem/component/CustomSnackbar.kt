package com.hyeeyoung.wishboard.designsystem.component

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_FADE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.SnackbarItemBinding

// TODO rename WishboardSnackbar
class CustomSnackbar(
    view: View,
    private val message: String,
    private val isTop: Boolean,
    private val callback: BaseTransientBottomBar.BaseCallback<Snackbar>?,
    private val duration: Int
) {
    companion object {
        fun make(
            view: View,
            message: String,
            isTop: Boolean = true,
            callback: BaseTransientBottomBar.BaseCallback<Snackbar>? = null,
            duration: Int = LENGTH_SHORT,
        ) = CustomSnackbar(view, message, isTop, callback, duration)
    }

    private val context = view.context
    private val snackbarBinding: SnackbarItemBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.snackbar_item, null, false)
    private val snackbar = Snackbar.make(view, "", duration)
    private val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
    private val layoutParams = LinearLayout.LayoutParams(snackbar.view.layoutParams)

    init {
        initializeView()
        initializeData()
    }

    private fun initializeView() {
        /* TODO 상단에서 떨어지고 올라가는 애니메이션(start: top-down, end: down-top) 적용
            스낵바 default위치가 bottom인데, top으로 조정할 경우, 스낵바는 상단에 뜨지만 애니메이션은 여전히 하단에서 올라오고 떨어짐(defaylt)
            임시방편 : 애니메이션을 Toast와 동일한 FADE효과(제자리에서 사라짐)로 지정 */
        // snackbar.view.animation = AnimationUtils.loadAnimation(context, R.anim.dropdown)

        snackbar.animationMode = ANIMATION_MODE_FADE
        with(snackbarLayout) {
            removeAllViews()

            // 스낵바 상단에 띄우기
//            if (isTop) {
//                this@CustomSnackbar.layoutParams.gravity = Gravity.TOP
//                layoutParams = this@CustomSnackbar.layoutParams
//            }
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            addView(snackbarBinding.root, 0)
        }
    }

    private fun initializeData() {
        snackbarBinding.message.text = message

        if (callback == null) return
        snackbar.addCallback(callback)
    }

    fun show() {
        snackbar.show()
    }

    fun dismiss() {
        if (duration == Snackbar.LENGTH_INDEFINITE) snackbar.dismiss()
    }
}