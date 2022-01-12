package com.hyeeyoung.wishboard.view.wish.item.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentWishItemDetailBinding
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.viewmodel.WishViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WishItemDetailFragment : Fragment() {
    private lateinit var binding: FragmentWishItemDetailBinding
    private val viewModel: WishViewModel by hiltNavGraphViewModels(R.id.wish_item_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishItemDetailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@WishItemDetailFragment

        arguments?.let {
            val wishItem = it[ARG_WISH_ITEM] as? WishItem
            if (wishItem != null) {
                viewModel.setWishItem(wishItem)
            }
        }

        addObservers()

        return binding.root
    }

    private fun addObservers() {
        viewModel.getWishItem().observe(viewLifecycleOwner) { wishItem ->
            if (wishItem == null) return@observe
            binding.goToShopBtn.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(wishItem.url)))
            }
            Glide.with(requireContext()).load(wishItem.image).into(binding.itemImage)
        }
    }

    companion object {
        const val ARG_WISH_ITEM = "wishItem"
    }
}