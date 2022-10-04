package com.hyeeyoung.wishboard.presentation.noti.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentNotiCalendarBinding
import com.hyeeyoung.wishboard.data.model.noti.NotiItem
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
import com.hyeeyoung.wishboard.presentation.noti.adapters.CalendarAdapter
import com.hyeeyoung.wishboard.presentation.noti.adapters.NotiListAdapter
import com.hyeeyoung.wishboard.presentation.noti.NotiViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.joda.time.LocalDate

@AndroidEntryPoint
class NotiCalendarFragment : Fragment(), NotiListAdapter.OnItemClickListener {
    private lateinit var binding: FragmentNotiCalendarBinding
    private lateinit var calendarAdapter: CalendarAdapter
    private val viewModel: NotiViewModel by hiltNavGraphViewModels(R.id.noti_calendar_nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchAllNotiList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotiCalendarBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initializeView()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        calendarAdapter = CalendarAdapter(requireActivity())

        binding.calendar.apply {
            this.adapter = calendarAdapter
            setCurrentItem(CalendarAdapter.START_POSITION, false)
        }

        val adapter = viewModel.getCalendarNotiListAdapter()
        adapter.setOnItemClickListener(this)

        binding.notiList.apply {
            this.adapter = adapter
            itemAnimator = null
            setItemViewCacheSize(20)
        }
    }

    private fun addObservers() {
        viewModel.getNotiList().observe(viewLifecycleOwner) {
            viewModel.setSelectedNotiList(LocalDate.now().toString())
        }
    }

    override fun onItemClick(position: Int, item: NotiItem) {
        if (item.itemUrl == null) {
            CustomSnackbar.make(binding.layout, getString(R.string.noti_item_url_snackbar_text)).show()
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.itemUrl)))
        }
    }

    companion object {
        private const val TAG = "NotiCalendarFragment"
    }
}