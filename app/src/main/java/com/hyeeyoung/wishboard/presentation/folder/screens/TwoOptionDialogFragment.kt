package com.hyeeyoung.wishboard.presentation.folder.screens

import android.os.Bundle
import android.view.View
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogTwoOptionBinding
import com.hyeeyoung.wishboard.presentation.base.screen.BaseBottomSheetDialogFragment
import com.hyeeyoung.wishboard.presentation.folder.types.TwoOptionDialogReplyType
import com.hyeeyoung.wishboard.util.DialogListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TwoOptionDialogFragment :
    BaseBottomSheetDialogFragment<DialogTwoOptionBinding>(R.layout.dialog_two_option) {
    private lateinit var listener: DialogListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments.let {
            binding.topOption = it?.getString(ARG_TOP_OPTION)
            binding.bottomOption = it?.getString(ARG_BOTTOM_OPTION)
        }

        addListeners()
    }

    private fun addListeners() {
        binding.btnTopOption.setOnClickListener {
            listener.onButtonClicked(TwoOptionDialogReplyType.TOP_OPTION.name)
        }
        binding.btnBottomOption.setOnClickListener {
            listener.onButtonClicked(TwoOptionDialogReplyType.BOTTOM_OPTION.name)
        }
        binding.cancel.setOnClickListener {
            listener.onButtonClicked(TwoOptionDialogReplyType.CANCEL.name)
        }
    }

    fun setListener(listener: DialogListener) {
        this.listener = listener
    }

    companion object {
        private const val ARG_TOP_OPTION = "topOption"
        private const val ARG_BOTTOM_OPTION = "bottomOption"

        fun newInstance(topOption: String, bottomOption: String): TwoOptionDialogFragment {
            val arg = Bundle().apply {
                putString(ARG_TOP_OPTION, topOption)
                putString(ARG_BOTTOM_OPTION, bottomOption)
            }
            return TwoOptionDialogFragment().apply { arguments = arg }
        }
    }
}