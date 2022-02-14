package com.hyeeyoung.wishboard.view.wish.item.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentWishItemDetailBinding
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.util.ImageLoader
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.loadImage
import com.hyeeyoung.wishboard.viewmodel.WishItemViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WishItemDetailFragment : Fragment(), ImageLoader {
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
                loadImage(wishItem.image ?: return@let, binding.itemImage)
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

        // 아이템 수정에서 수정 완료 후 상세조회로 복귀했을 때 해당 아이템 정보를 전달받고, ui 업데이트
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<WishItem>(
            ARG_WISH_ITEM
        )?.observe(viewLifecycleOwner) { item ->
            // TODO 이미지가 업데이트 된 경우, 해당 이미지 이름으로 S3에서 이미지 다운로드 받아야함.
            //  매번 다운로드받기 번거롭기 때문에 다운로드 없이 이미지를 로드하는 방법을 고민하고 있음.
            viewModel.setWishItem(item)
            loadImage(item.image ?: return@observe, binding.itemImage)
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

    override fun loadImage(imageUrl: String, imageView: ImageView) {
        loadImage(lifecycleScope, imageUrl, imageView)
    }

    companion object {
        private const val TAG = "WishItemDetailFragment"
        private const val ARG_WISH_ITEM = "wishItem"
        private const val ARG_WISH_ITEM_POSITION = "position"
        private const val ARG_WISH_ITEM_INFO = "wishItemInfo"
        private const val ARG_IS_EDIT_MODE = "isEditMode"
    }
}