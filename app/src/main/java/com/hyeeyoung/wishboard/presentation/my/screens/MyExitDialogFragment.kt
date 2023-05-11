package com.hyeeyoung.wishboard.presentation.my.screens

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogMyExitBinding
import com.hyeeyoung.wishboard.presentation.base.screen.BaseDialogFragment
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.presentation.my.MyViewModel
import com.hyeeyoung.wishboard.util.DialogListener

class MyExitDialogFragment : BaseDialogFragment<DialogMyExitBinding>(R.layout.dialog_my_exit) {
    private lateinit var listener: DialogListener
    private val viewModel: MyViewModel by hiltNavGraphViewModels(R.id.my_nav_graph)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.resetCorrectedEmail()

        addListener()
    }

    private fun addListener() {
        binding.yes.setOnClickListener {
            val isCorrect = viewModel.checkCorrectedEmail()
            if (isCorrect) {
                listener.onButtonClicked(DialogButtonReplyType.YES.name)
            }
        }
        binding.no.setOnClickListener {
            dismiss()
        }
    }

    fun setListener(listener: DialogListener) {
        this.listener = listener
    }
}