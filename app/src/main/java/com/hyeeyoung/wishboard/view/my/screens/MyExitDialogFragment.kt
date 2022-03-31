package com.hyeeyoung.wishboard.view.my.screens

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogMyExitBinding
import com.hyeeyoung.wishboard.model.common.DialogButtonReplyType
import com.hyeeyoung.wishboard.view.common.screens.DialogListener
import com.hyeeyoung.wishboard.viewmodel.MyViewModel

class MyExitDialogFragment() : DialogFragment() {
    private lateinit var binding: DialogMyExitBinding
    private lateinit var listener: DialogListener
    private val viewModel: MyViewModel by hiltNavGraphViewModels(R.id.my_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogMyExitBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@MyExitDialogFragment

        viewModel.resetCorrectedEmail()

        val dialog = dialog
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        addListener()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
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