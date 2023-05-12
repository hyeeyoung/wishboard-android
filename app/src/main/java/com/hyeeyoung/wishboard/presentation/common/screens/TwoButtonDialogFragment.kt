package com.hyeeyoung.wishboard.presentation.common.screens

import android.os.Bundle
import android.view.View
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogTwoButtonBinding
import com.hyeeyoung.wishboard.presentation.base.screen.BaseDialogFragment
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.util.DialogListener

class TwoButtonDialogFragment(
    private val title: String,
    private val description: String?,
    private val yesValue: String,
    private val noValue: String,
) : BaseDialogFragment<DialogTwoButtonBinding>(R.layout.dialog_two_button) {
    private lateinit var listener: DialogListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = title
        binding.description.text = description
        binding.yes.text = yesValue
        binding.no.text = noValue

        addListener()
    }

    private fun addListener() {
        binding.yes.setOnClickListener {
            listener.onButtonClicked(DialogButtonReplyType.YES.name)
        }
        binding.no.setOnClickListener {
            listener.onButtonClicked(DialogButtonReplyType.NO.name)
        }
    }

    fun setListener(listener: DialogListener) {
        this.listener = listener
    }
}