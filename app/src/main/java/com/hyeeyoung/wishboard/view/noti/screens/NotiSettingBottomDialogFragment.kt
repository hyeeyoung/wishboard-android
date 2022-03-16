package com.hyeeyoung.wishboard.view.noti.screens

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogBottomNotiSettingBinding
import com.hyeeyoung.wishboard.util.*
import com.hyeeyoung.wishboard.viewmodel.WishItemRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotiSettingBottomDialogFragment : DialogFragment() {
    private lateinit var binding: DialogBottomNotiSettingBinding
    private val viewModel: WishItemRegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomNotiSettingBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@NotiSettingBottomDialogFragment

        initializeView()
        addListeners()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            resources.getDimensionPixelSize(R.dimen.widgetBottomDialogHeightBase)
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }

    private fun initializeView() {
        // 알림 유형 및 날짜 넘버피커 설정
        setTypePicker(binding.typePicker)
        setDatePicker(binding.datePicker)
        setHourPicker(binding.hourPicker)
        setMinutePicker(binding.minutePicker)
    }

    private fun addListeners() {
        binding.complete.setOnClickListener {
            val type = getTypePickerValue(binding.typePicker.value)
            val date = getDatePickerValue(
                binding.datePicker.value,
                binding.hourPicker.value,
                binding.minutePicker.value
            )
            viewModel.setNotiInfo(true, type, date)
            dismiss()
        }
        binding.back.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "NotiSettingBottomDialogFragment"
    }
}