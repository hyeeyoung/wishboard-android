package com.hyeeyoung.wishboard.view.home.wishitem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentWishItemDetailBinding
import com.hyeeyoung.wishboard.viewmodel.WishViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WishItemDetailFragment : Fragment() {
    private lateinit var binding: FragmentWishItemDetailBinding
    private val viewModel: WishViewModel by hiltNavGraphViewModels(R.id.wish_item_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWishItemDetailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@WishItemDetailFragment

        arguments?.let {
            it[ARG_ITEM_ID]?.let { itemId ->
                viewModel.fetchItemDetail(itemId as Int)
            }
        }

        addObservers()

        return binding.root
    }

    private fun addObservers() {
        // TODO 코드 변경 필요
        viewModel.getWishItem().observe(viewLifecycleOwner) { itemInfo ->
            if (itemInfo == null) return@observe
            binding.goToShopBtn.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(itemInfo.itemUrl)))
            }
            Glide.with(requireContext()).load(itemInfo.image).into(binding.itemImage)
        }
    }

    companion object {
        const val ARG_ITEM_ID = "itemId"
    }
}