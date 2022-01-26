package com.hyeeyoung.wishboard.view.wish.item.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentWishItemDetailBinding
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.viewmodel.WishItemViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WishItemDetailFragment : Fragment() {
    private lateinit var binding: FragmentWishItemDetailBinding
    private val viewModel: WishItemViewModel by hiltNavGraphViewModels(R.id.wish_item_nav_graph)
    private var position: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishItemDetailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@WishItemDetailFragment

        arguments?.let {
            val wishItem = it[ARG_WISH_ITEM] as? WishItem
            position = it[ARG_WISH_ITEM_POSITION] as? Int

            if (wishItem != null) {
                viewModel.setWishItem(wishItem)
            }
        }

        addListeners()
        addObservers()

        return binding.root
    }

    private fun addListeners() {
        binding.edit.setOnClickListener {
            findNavController().navigateSafe(R.id.action_detail_to_registration, bundleOf(
                ARG_WISH_ITEM to viewModel.getWishItem().value,
                ARG_IS_EDIT_MODE to true
            ))
        }
    }

    private fun addObservers() {
        viewModel.getWishItem().observe(viewLifecycleOwner) { wishItem ->
            if (wishItem == null) return@observe
            binding.goToShopBtn.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(wishItem.url)))
            }
            Glide.with(requireContext()).load(wishItem.image).into(binding.itemImage)
        }

        viewModel.getIsCompleteDeletion().observe(viewLifecycleOwner) { isComplete ->
            if (isComplete == true) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.wish_item_deletion_toast_text),
                    Toast.LENGTH_SHORT
                ).show()

                moveToMain()
            }
        }
    }

    private fun moveToMain() {
        val navController = findNavController()
        navController.previousBackStackEntry?.savedStateHandle?.set(ARG_WISH_ITEM_INFO, bundleOf(
            ARG_WISH_ITEM to viewModel.getWishItem().value,
            ARG_WISH_ITEM_POSITION to position
        ))
        navController.popBackStack()
    }

    companion object {
        private const val TAG = "WishItemDetailFragment"
        private const val ARG_WISH_ITEM = "wishItem"
        private const val ARG_WISH_ITEM_POSITION = "position"
        private const val ARG_WISH_ITEM_INFO = "wishItemInfo"
        private const val ARG_IS_EDIT_MODE = "isEditMode"
    }
}