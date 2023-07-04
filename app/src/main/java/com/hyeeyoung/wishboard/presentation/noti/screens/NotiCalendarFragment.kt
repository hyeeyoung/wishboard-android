package com.hyeeyoung.wishboard.presentation.noti.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentNotiCalendarBinding
import com.hyeeyoung.wishboard.domain.model.NotiItemInfo
import com.hyeeyoung.wishboard.presentation.base.screen.NetworkFragment
import com.hyeeyoung.wishboard.presentation.noti.NotiViewModel
import com.hyeeyoung.wishboard.presentation.noti.adapters.CalendarAdapter
import com.hyeeyoung.wishboard.presentation.noti.adapters.NotiListAdapter
import com.hyeeyoung.wishboard.util.UiState
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
import com.hyeeyoung.wishboard.util.extension.collectFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import org.joda.time.LocalDate

@AndroidEntryPoint
class NotiCalendarFragment :
    NetworkFragment<FragmentNotiCalendarBinding>(R.layout.fragment_noti_calendar),
    NotiListAdapter.OnItemClickListener {
    private lateinit var calendarAdapter: CalendarAdapter
    private val viewModel: NotiViewModel by hiltNavGraphViewModels(R.id.noti_calendar_nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchAllNotiList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initializeView()
        addObservers()
        collectData()
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

    private fun collectData() {
        collectFlow(combine(isConnected, viewModel.notiFetchState) { isConnected, isSuccessful ->
            isConnected && isSuccessful !is UiState.Success
        }) { shouldFetch ->
            if (shouldFetch) viewModel.fetchAllNotiList()
        }
    }

    override fun onItemClick(position: Int, item: NotiItemInfo) {
        if (item.itemUrl == null) {
            CustomSnackbar.make(binding.layout, getString(R.string.noti_item_url_snackbar_text)).show()
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.itemUrl)))
        }
    }
}