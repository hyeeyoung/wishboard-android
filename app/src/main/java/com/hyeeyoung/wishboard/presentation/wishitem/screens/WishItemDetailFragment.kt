package com.hyeeyoung.wishboard.presentation.wishitem.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentWishItemDetailBinding
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.presentation.wishitem.WishItemStatus
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.safeLet
import com.hyeeyoung.wishboard.util.DialogListener
import com.hyeeyoung.wishboard.presentation.common.screens.TwoButtonDialogFragment
import com.hyeeyoung.wishboard.util.FolderListDialogListener
import com.hyeeyoung.wishboard.presentation.folder.screens.FolderListBottomDialogFragment
import com.hyeeyoung.wishboard.presentation.wishitem.viewmodels.WishItemViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WishItemDetailFragment : Fragment() {
    private lateinit var binding: FragmentWishItemDetailBinding
    private val viewModel: WishItemViewModel by viewModels()
    private var wishItem: WishItem? = null
    private var position: Int? = null // TODO delete
    private var itemStatus: WishItemStatus? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishItemDetailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@WishItemDetailFragment

        arguments?.let {
            position = it[ARG_WISH_ITEM_POSITION] as? Int

            (it[ARG_WISH_ITEM] as? WishItem)?.let { item ->
                wishItem = item
                viewModel.setWishItem(item)
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
            findNavController().navigateSafe(
                R.id.action_detail_to_registration, bundleOf(
                    ARG_WISH_ITEM to viewModel.getWishItem().value,
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
    }

    private fun addObservers() {
        viewModel.getWishItem().observe(viewLifecycleOwner) { item ->
            Glide.with(binding.itemImage).load(item.imageUrl).into(binding.itemImage)
            binding.goToShopBtn.setOnClickListener {
                if (item.url == null) return@setOnClickListener
                goToShop(item.url)
            }
        }

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
        )?.observe(viewLifecycleOwner) {
            safeLet(
                it[ARG_ITEM_STATUS] as? WishItemStatus,
                it[ARG_WISH_ITEM] as? WishItem
            ) { status, item ->
                itemStatus = status
                viewModel.setWishItem(item)
            }
            return@observe
        }
    }

    private fun moveToPrevious(itemStatus: WishItemStatus) {
        val navController = findNavController()
        navController.previousBackStackEntry?.savedStateHandle?.set(
            ARG_WISH_ITEM_INFO, bundleOf(
                ARG_ITEM_STATUS to itemStatus,
                ARG_WISH_ITEM to viewModel.getWishItem().value,
                ARG_WISH_ITEM_POSITION to position
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
        val folderId = wishItem?.folderId
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
        private const val TAG = "WishItemDetailFragment"
        private const val ARG_WISH_ITEM = "wishItem"
        private const val ARG_WISH_ITEM_POSITION = "position"
        private const val ARG_WISH_ITEM_INFO = "wishItemInfo"
        private const val ARG_IS_EDIT_MODE = "isEditMode"
        private const val ARG_ITEM_STATUS = "itemStatus"
    }
}