package com.hyeeyoung.wishboard.presentation.wishitem.screens

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.DialogBottomShopLinkInputBinding
import com.hyeeyoung.wishboard.presentation.base.screen.BaseBottomSheetDialogFragment
import com.hyeeyoung.wishboard.presentation.wishitem.viewmodels.WishItemRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopLinkInputBottomDialogFragment :
    BaseBottomSheetDialogFragment<DialogBottomShopLinkInputBinding>(R.layout.dialog_bottom_shop_link_input) {
    private val viewModel: WishItemRegistrationViewModel by hiltNavGraphViewModels(R.id.wish_item_registration_nav_graph)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // 키보드 위로 다이얼로그를 띄우기 위함
        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                sheet.parent.parent.requestLayout()
            }
        }

        addListeners()
        addObservers()
    }

    private fun addListeners() {
        binding.getItem.setOnClickListener {
            viewModel.loadWishItemInfo()
        }
        binding.back.setOnClickListener {
            dismiss()
        }
    }

    private fun addObservers() {
        viewModel.getIsValidItemUrl().observe(viewLifecycleOwner) {
            if (it == true) {
                // TODO need refactoring
                viewModel.removeWishItemImage()
                dismiss()
            }
        }
    }

    override fun dismiss() {
        super.dismiss()
        viewModel.resetValidItemUrl()
    }
}