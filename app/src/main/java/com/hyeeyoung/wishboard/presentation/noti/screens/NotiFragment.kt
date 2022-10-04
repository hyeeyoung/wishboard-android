package com.hyeeyoung.wishboard.presentation.noti.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentNotiBinding
import com.hyeeyoung.wishboard.data.model.noti.NotiItem
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
import com.hyeeyoung.wishboard.presentation.noti.adapters.NotiListAdapter
import com.hyeeyoung.wishboard.presentation.noti.NotiViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotiFragment : Fragment(), NotiListAdapter.OnItemClickListener {
    private lateinit var binding: FragmentNotiBinding
    private val viewModel: NotiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchPreviousNotiList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotiBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initializeView()
        addListeners()

        return binding.root
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

    override fun onItemClick(position: Int, item: NotiItem) {
        viewModel.updateNotiReadState(position, item.itemId)
        if (item.itemUrl == null) {
            CustomSnackbar.make(binding.layout, getString(R.string.noti_item_url_snackbar_text)).show()
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.itemUrl)))
        }
    }

    companion object {
        private const val TAG = "NotiFragment"
    }
}