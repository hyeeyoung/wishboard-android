package com.hyeeyoung.wishboard.presentation.common.screens

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.hyeeyoung.wishboard.databinding.DialogOneButtonBinding

class OneButtonDialogFragment(
    private val title: String,
    private val description: String?
) : DialogFragment() {
    private lateinit var binding: DialogOneButtonBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogOneButtonBinding.inflate(inflater, container, false)
        val dialog = dialog
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        binding.title.text = title
        binding.description.text = description

        addListener()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun addListener() {
        binding.yes.setOnClickListener {
            dismiss()
        }
    }
}