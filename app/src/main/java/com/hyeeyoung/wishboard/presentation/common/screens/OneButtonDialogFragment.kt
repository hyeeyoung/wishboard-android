package com.hyeeyoung.wishboard.presentation.common.screens

import android.os.Bundle
import android.view.View
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogOneButtonBinding
import com.hyeeyoung.wishboard.presentation.base.screen.BaseDialogFragment
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.util.DialogListener

class OneButtonDialogFragment :
    BaseDialogFragment<DialogOneButtonBinding>(R.layout.dialog_one_button) {
    private lateinit var listener: DialogListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            binding.title.text = it.getString(ARG_TITLE)
            binding.description.text = it.getString(ARG_DESCRIPTION)
            binding.yes.text = it.getString(ARG_BUTTON_TEXT) ?: getString(R.string.confirm)
        }

        addListener()
    }

    private fun addListener() {
        binding.yes.setOnClickListener {
            listener.onButtonClicked(DialogButtonReplyType.YES.name)
        }
    }

    fun setListener(listener: DialogListener) {
        this.listener = listener
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_BUTTON_TEXT = "buttonText"

        fun newInstance(
            title: String,
            description: String?,
            buttonText: String? = null,
        ): OneButtonDialogFragment {
            val args = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_DESCRIPTION, description)
                putString(ARG_BUTTON_TEXT, buttonText)
            }

            return OneButtonDialogFragment().apply { arguments = args }
        }
    }
}