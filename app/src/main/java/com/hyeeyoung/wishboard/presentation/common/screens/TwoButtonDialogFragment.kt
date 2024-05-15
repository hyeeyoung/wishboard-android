package com.hyeeyoung.wishboard.presentation.common.screens

import android.os.Bundle
import android.view.View
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogTwoButtonBinding
import com.hyeeyoung.wishboard.presentation.base.screen.BaseDialogFragment
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.util.DialogListener

class TwoButtonDialogFragment :
    BaseDialogFragment<DialogTwoButtonBinding>(R.layout.dialog_two_button) {
    private lateinit var listener: DialogListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            binding.title.text = it.getString(ARG_TITLE)
            binding.description.text = it.getString(ARG_DESCRIPTION)
            binding.yes.text = it.getString(ARG_YES_VALUE)
            binding.no.text = it.getString(ARG_NO_VALUE)
            val isWarningDialog = it.getBoolean(ARG_WARNING_DIALOG)
            binding.yes.setTextColor(requireContext().getColor(if (isWarningDialog) R.color.pink else R.color.green_700))
        }

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

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_YES_VALUE = "yesValue"
        private const val ARG_NO_VALUE = "noValue"
        private const val ARG_WARNING_DIALOG = "isWarningDialog"

        fun newInstance(
            title: String,
            description: String?,
            yesValue: String,
            noValue: String,
            isWarningDialog: Boolean = true
        ): TwoButtonDialogFragment {
            val args = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_DESCRIPTION, description)
                putString(ARG_YES_VALUE, yesValue)
                putString(ARG_NO_VALUE, noValue)
                putBoolean(ARG_WARNING_DIALOG, isWarningDialog)
            }

            return TwoButtonDialogFragment().apply { arguments = args }
        }
    }
}