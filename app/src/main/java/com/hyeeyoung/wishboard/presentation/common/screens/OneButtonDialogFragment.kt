package com.hyeeyoung.wishboard.presentation.common.screens

import android.os.Bundle
import android.view.View
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogOneButtonBinding
import com.hyeeyoung.wishboard.presentation.base.screen.BaseDialogFragment

class OneButtonDialogFragment(
    private val title: String,
    private val description: String?
) : BaseDialogFragment<DialogOneButtonBinding>(R.layout.dialog_one_button) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = title
        binding.description.text = description

        addListener()
    }

    private fun addListener() {
        binding.yes.setOnClickListener {
            dismiss()
        }
    }
}