package com.hyeeyoung.wishboard.view.common.screens

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.hyeeyoung.wishboard.databinding.DialogTwoButtonBinding
import com.hyeeyoung.wishboard.model.common.DialogButtonReplyType

class TwoButtonDialogFragment(
    private val title: String,
    private val description: String?,
    private val yesValue: String,
    private val noValue: String,
) : DialogFragment() {
    private lateinit var binding: DialogTwoButtonBinding
    private lateinit var listener: DialogListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogTwoButtonBinding.inflate(inflater, container, false)
        val dialog = dialog
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        binding.title.text = title
        binding.description.text = description
        binding.yes.text = yesValue
        binding.no.text = noValue

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