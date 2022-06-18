package com.hyeeyoung.wishboard.presentation.noti.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentCalendarBinding
import com.hyeeyoung.wishboard.util.CalendarUtils.Companion.getMonthList
import com.hyeeyoung.wishboard.presentation.noti.NotiViewModel
import org.joda.time.DateTime

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private val viewModel: NotiViewModel by hiltNavGraphViewModels(R.id.noti_calendar_nav_graph)
    private var millis: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            millis = it.getLong(MILLIS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@CalendarFragment

        initializeView()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        binding.calendarView.initCalendar(DateTime(millis), getMonthList(DateTime(millis)), viewModel)
    }

    private fun addObservers() {
        viewModel.getNotiDateList().observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) binding.calendarView.showNotiMark(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setCalendarMonthTitle(millis)
    }

    companion object {
        private const val TAG = "CalendarFragment"
        private const val MILLIS = "MILLIS"
        fun newInstance(millis: Long) = CalendarFragment().apply {
            arguments = Bundle().apply {
                putLong(MILLIS, millis)
            }
        }
    }
}
