package com.hyeeyoung.wishboard.view.noti.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentNotiCalendarBinding
import com.hyeeyoung.wishboard.view.noti.adapters.CalendarAdapter
import com.hyeeyoung.wishboard.viewmodel.NotiViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotiCalendarFragment : Fragment(){
    private lateinit var binding: FragmentNotiCalendarBinding
    private lateinit var calendarAdapter: CalendarAdapter
    private val viewModel: NotiViewModel by hiltNavGraphViewModels(R.id.noti_calendar_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotiCalendarBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@NotiCalendarFragment

        initializeView()

        return binding.root
    }

    private fun initializeView() {
        calendarAdapter = CalendarAdapter(requireActivity())

        binding.calendar.apply {
            this.adapter = calendarAdapter
            setCurrentItem(CalendarAdapter.START_POSITION, false)
        }
    }

    companion object {
        private const val TAG = "NotiCalendarFragment"
    }
}