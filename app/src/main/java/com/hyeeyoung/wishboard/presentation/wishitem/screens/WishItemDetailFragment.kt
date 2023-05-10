package com.hyeeyoung.wishboard.presentation.wishitem.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.databinding.FragmentWishItemDetailBinding
import com.hyeeyoung.wishboard.presentation.base.screen.NetworkFragment
import com.hyeeyoung.wishboard.presentation.common.screens.TwoButtonDialogFragment
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.presentation.folder.screens.FolderListBottomDialogFragment
import com.hyeeyoung.wishboard.presentation.wishitem.WishItemStatus
import com.hyeeyoung.wishboard.presentation.wishitem.viewmodels.WishItemViewModel
import com.hyeeyoung.wishboard.util.DialogListener
import com.hyeeyoung.wishboard.util.FolderListDialogListener
import com.hyeeyoung.wishboard.util.UiState
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
import com.hyeeyoung.wishboard.util.extension.collectFlow
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.extension.safeValueOf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine

@AndroidEntryPoint
class WishItemDetailFragment :
    NetworkFragment<FragmentWishItemDetailBinding>(R.layout.fragment_wish_item_detail) {
    override val viewModel: WishItemViewModel by viewModels()
    private var itemStatus: WishItemStatus? = null
    private var itemId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            (it.getLong(ARG_WISH_ITEM_ID)).let { id ->
                itemId = id
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initializeView()
        addListeners()
        addObservers()
        collectData()
    }

    private fun initializeView() {
        binding.itemImage.clipToOutline = true
    }

    private fun addListeners() {
        binding.delete.setOnClickListener {
            showItemDeleteDialog()
        }
        binding.edit.setOnClickListener {
            findNavController().navigateSafe(
                R.id.action_detail_to_registration, bundleOf(
                    ARG_WISH_ITEM_DETAIL to viewModel.itemDetail.value,
                    ARG_IS_EDIT_MODE to true
                )
            )
        }
        binding.back.setOnClickListener {
            when (itemStatus) {
                null -> findNavController().popBackStack()
                else -> moveToPrevious(itemStatus!!)
            }
        }
        binding.folderName.setOnClickListener {
            showFolderListDialog()
        }
        binding.goToShopBtn.setOnClickListener {
            viewModel.itemDetail.value?.site?.let { site -> goToShop(site) }
        }
    }

    private fun addObservers() {
        viewModel.getIsCompleteDeletion().observe(viewLifecycleOwner) { isComplete ->
            if (isComplete == true) {
                CustomSnackbar.make(
                    binding.layout,
                    getString(R.string.wish_item_deletion_snackbar_text)
                ).show()
                moveToPrevious(WishItemStatus.DELETED)
            }
        }

        // 아이템 수정에서 수정 완료 후 상세조회로 복귀했을 때 해당 아이템 정보를 전달받고, ui 업데이트
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(
            ARG_WISH_ITEM_INFO
        )?.observe(viewLifecycleOwner) { bundle ->
            val status =
                safeValueOf<WishItemStatus>(bundle.getString(ARG_ITEM_STATUS)) ?: return@observe
            if (status == WishItemStatus.MODIFIED)
                viewModel.requestFetchWishDetail()
            itemStatus = status
            return@observe
        }
    }

    private fun collectData() {
        collectFlow(
            combine(
                viewModel.isConnected,
                viewModel.wishDetailFetchState
            ) { isConnected, isSuccessful ->
                isConnected && isSuccessful !is UiState.Success
            }) { shouldFetch ->
            if (shouldFetch) viewModel.fetchWishItemDetail(itemId)
        }
    }

    private fun moveToPrevious(itemStatus: WishItemStatus) {
        val navController = findNavController()
        navController.previousBackStackEntry?.savedStateHandle?.set(
            ARG_WISH_ITEM_INFO, bundleOf(
                ARG_ITEM_STATUS to itemStatus.name,
                ARG_WISH_ITEM_THUMBNAIL to viewModel.wishItemThumbnail.value,
            )
        )
        navController.popBackStack()
    }

    private fun showItemDeleteDialog() {
        val dialog = TwoButtonDialogFragment(
            getString(R.string.item_detail_item_delete_dialog_title),
            getString(R.string.item_detail_item_delete_dialog_description),
            getString(R.string.delete),
            getString(R.string.cancel)
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

    private fun showFolderListDialog() {
        val folderId = viewModel.itemDetail.value?.folderId
        val dialog = FolderListBottomDialogFragment(folderId).apply {
            setListener(object : FolderListDialogListener {
                override fun onButtonClicked(folder: FolderItem) {
                    viewModel.updateWishItemFolder(folder)
                    dismiss()
                }
            })
        }
        dialog.show(parentFragmentManager, "FolderListDialog")
    }

    private fun goToShop(url: String) {
        if (Patterns.WEB_URL.matcher(url).matches()) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                return
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }

        CustomSnackbar.make(
            binding.layout,
            requireContext().getString(R.string.item_detail_invalid_site_snackbar_text)
        ).show()
    }

    companion object {
        private const val ARG_WISH_ITEM_THUMBNAIL = "wishItemThumbnail"
        private const val ARG_WISH_ITEM_DETAIL = "wishItemDetail"
        private const val ARG_WISH_ITEM_ID = "wishItemId"
        private const val ARG_WISH_ITEM_INFO = "wishItemInfo"
        private const val ARG_IS_EDIT_MODE = "isEditMode"
        private const val ARG_ITEM_STATUS = "itemStatus"
    }
}