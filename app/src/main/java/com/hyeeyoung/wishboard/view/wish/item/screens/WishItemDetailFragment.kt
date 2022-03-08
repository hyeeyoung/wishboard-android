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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentWishItemDetailBinding
import com.hyeeyoung.wishboard.model.common.DialogButtonReplyType
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.service.AWSS3Service
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.view.common.screens.DialogListener
import com.hyeeyoung.wishboard.view.common.screens.TwoButtonDialogFragment
import com.hyeeyoung.wishboard.viewmodel.WishItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

            wishItem?.let { item ->
                viewModel.setWishItem(item)

                Glide.with(binding.itemImage).load(item.imageUrl).into(binding.itemImage)
                binding.goToShopBtn.setOnClickListener { // TODO need refactoring
                    if (item.url == null) return@setOnClickListener
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.url)))
                }
            }
        }

        initializeView()
        addListeners()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        binding.itemImage.clipToOutline = true
    }

    private fun addListeners() {
        binding.delete.setOnClickListener {
            showItemDeleteDialog()
        }
        binding.edit.setOnClickListener {
            findNavController().navigateSafe(R.id.action_detail_to_registration, bundleOf(
                ARG_WISH_ITEM to viewModel.getWishItem().value,
                ARG_IS_EDIT_MODE to true
            ))
        }
    }

    private fun addObservers() {
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
            // 이미지가 업데이트 된 경우, 해당 이미지 이름으로 S3에서 이미지 다운로드 받기
            item.image?.let { image ->
                lifecycleScope.launch {
                    val imageUrl = AWSS3Service().getImageUrl(image)
                    Glide.with(binding.itemImage).load(imageUrl).into(binding.itemImage)
                }
            }

            viewModel.setWishItem(item)
            return@observe
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

    private fun showItemDeleteDialog() {
        val dialog = TwoButtonDialogFragment(
            getString(R.string.item_detail_item_delete_dialog_title),
            getString(R.string.item_detail_item_delete_dialog_description),
            getString(R.string.delete)
        ).apply {
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: String) {
                    if (clicked == DialogButtonReplyType.YES.name) {
                        viewModel.deleteWishItem()
                    }
                    dismiss()
                }
            })
        }
        dialog.show(parentFragmentManager, "ItemDeleteDialog")
    }

    companion object {
        private const val TAG = "WishItemDetailFragment"
        private const val ARG_WISH_ITEM = "wishItem"
        private const val ARG_WISH_ITEM_POSITION = "position"
        private const val ARG_WISH_ITEM_INFO = "wishItemInfo"
        private const val ARG_IS_EDIT_MODE = "isEditMode"
    }
}