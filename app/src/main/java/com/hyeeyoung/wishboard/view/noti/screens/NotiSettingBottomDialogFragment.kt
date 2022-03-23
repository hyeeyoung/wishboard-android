package com.hyeeyoung.wishboard.view.noti.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyeeyoung.wishboard.databinding.DialogBottomNotiSettingBinding
import com.hyeeyoung.wishboard.util.*
import com.hyeeyoung.wishboard.viewmodel.WishItemRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotiSettingBottomDialogFragment(private val viewModel: WishItemRegistrationViewModel) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogBottomNotiSettingBinding

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