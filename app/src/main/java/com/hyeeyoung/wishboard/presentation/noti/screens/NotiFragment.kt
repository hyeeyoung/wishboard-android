package com.hyeeyoung.wishboard.presentation.noti.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentNotiBinding
import com.hyeeyoung.wishboard.domain.model.NotiItemInfo
import com.hyeeyoung.wishboard.presentation.base.screen.NetworkFragment
import com.hyeeyoung.wishboard.presentation.noti.NotiViewModel
import com.hyeeyoung.wishboard.presentation.noti.NotiListAdapter
import com.hyeeyoung.wishboard.util.UiState
import com.hyeeyoung.wishboard.designsystem.component.CustomSnackbar
import com.hyeeyoung.wishboard.util.extension.collectFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine

@AndroidEntryPoint
class NotiFragment : NetworkFragment<FragmentNotiBinding>(R.layout.fragment_noti),
    NotiListAdapter.OnItemClickListener {
    private val viewModel: NotiViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initializeView()
        addListeners()
        collectData()
    }

    private fun initializeView() {
        val adapter = viewModel.getNotiListAdapter()
        adapter.setOnItemClickListener(this)

        binding.notiList.apply {
            this.adapter = adapter
            itemAnimator = null
            setItemViewCacheSize(20)
        }
    }

    private fun addListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchPreviousNotiList()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun collectData() {
        collectFlow(combine(isConnected, viewModel.notiFetchState) { isConnected, isSuccessful ->
            isConnected && isSuccessful !is UiState.Success
        }) { shouldFetch ->
            if (shouldFetch) viewModel.fetchPreviousNotiList()
        }
    }

    override fun onItemClick(position: Int, item: NotiItemInfo) {
        viewModel.updateNotiReadState(position, item.itemId)
        if (item.itemUrl == null) {
            CustomSnackbar.make(binding.layout, getString(R.string.noti_item_url_snackbar_text))
                .show()
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.itemUrl)))
        }
    }
}