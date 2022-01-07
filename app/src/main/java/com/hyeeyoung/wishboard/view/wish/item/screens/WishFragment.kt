package com.hyeeyoung.wishboard.view.wish.item.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hyeeyoung.wishboard.databinding.FragmentWishBinding
import com.hyeeyoung.wishboard.viewmodel.WishItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WishFragment : Fragment() {
    private lateinit var binding: FragmentWishBinding
    private val viewModel: WishItemViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWishBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        addListeners()
        addObservers()

        return binding.root
    }

    private fun init() {
        binding.itemImage.setImageDrawable(null)
        binding.itemName.setText("")
        binding.itemPrice.setText("")
        binding.itemUrl.setText("")
        binding.itemMemo.setText("")
    }

    private fun addListeners() {
        binding.save.setOnClickListener {
            lifecycleScope.launch {
                viewModel.uploadWishItemByBasics()
            }
        }
    }

    private fun addObservers() {
        viewModel.isCompleteUpload().observe(viewLifecycleOwner) { isCompleted ->
            when (isCompleted) {
                true -> {
                    init()
                    viewModel.setCompletedUpload(null)
                }
            }
        }
    }

    companion object {
        private const val TAG = "WishFragment"
    }
}