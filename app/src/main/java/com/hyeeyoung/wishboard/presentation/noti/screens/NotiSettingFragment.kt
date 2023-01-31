package com.hyeeyoung.wishboard.presentation.noti.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentNotiSettingBinding
import com.hyeeyoung.wishboard.util.*
import com.hyeeyoung.wishboard.presentation.wishitem.viewmodels.WishItemRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotiSettingFragment : Fragment() {
    private lateinit var binding: FragmentNotiSettingBinding
    private val viewModel: WishItemRegistrationViewModel by hiltNavGraphViewModels(R.id.wish_item_registration_nav_graph)
    private var isCheckedNotiSwitch = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotiSettingBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

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