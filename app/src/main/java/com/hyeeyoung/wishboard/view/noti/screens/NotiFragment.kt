package com.hyeeyoung.wishboard.view.noti.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hyeeyoung.wishboard.databinding.FragmentNotiBinding
import com.hyeeyoung.wishboard.model.noti.NotiItem
import com.hyeeyoung.wishboard.view.noti.adapters.NotiListAdapter
import com.hyeeyoung.wishboard.viewmodel.NotiViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotiFragment : Fragment(), NotiListAdapter.OnItemClickListener {
    private lateinit var binding: FragmentNotiBinding
    private val viewModel: NotiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchNotiList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotiBinding.inflate(inflater, container, false)

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
            viewModel.fetchNotiList()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onItemClick(position: Int, item: NotiItem) {
        if (item.itemUrl == null) return
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.itemUrl)))
        viewModel.updateNotiReadState(position, item.itemId)
    }

    companion object {
        private const val TAG = "NotiFragment"
    }
}