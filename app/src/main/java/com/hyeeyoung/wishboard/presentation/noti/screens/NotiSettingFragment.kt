package com.hyeeyoung.wishboard.presentation.noti.screens

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentNotiSettingBinding
import com.hyeeyoung.wishboard.presentation.wishitem.viewmodels.WishItemRegistrationViewModel
import com.hyeeyoung.wishboard.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotiSettingFragment :
    BaseFragment<FragmentNotiSettingBinding>(R.layout.fragment_noti_setting) {
    private val viewModel: WishItemRegistrationViewModel by hiltNavGraphViewModels(R.id.wish_item_registration_nav_graph)
    private var isCheckedNotiSwitch = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        initializeView()
        addListeners()
    }

    private fun initializeView() {
        // 알림 유형 및 날짜 넘버피커 설정
        setTypePicker(binding.typePicker)
        setDatePicker(binding.datePicker)
        setHourPicker(binding.hourPicker)
        setMinutePicker(binding.minutePicker)

        binding.notiTypeHighlight.clipToOutline = true
        binding.notiDateHighlight.clipToOutline = true
    }

    private fun addListeners() {
        binding.save.setOnClickListener {
            val type = getTypePickerValue(binding.typePicker.value)
            val date = getDatePickerValue(
                binding.datePicker.value,
                binding.hourPicker.value,
                binding.minutePicker.value
            )
            viewModel.setNotiInfo(isCheckedNotiSwitch, type, date)
            findNavController().popBackStack()
        }

        binding.notiSwitch.setOnCheckedChangeListener { _, isChecked ->  // TODO need refactoring
            if (isChecked) {
                binding.notiContainer.visibility = View.VISIBLE
            } else {
                binding.notiContainer.visibility = View.INVISIBLE
            }
            isCheckedNotiSwitch = isChecked
        }
    }
}