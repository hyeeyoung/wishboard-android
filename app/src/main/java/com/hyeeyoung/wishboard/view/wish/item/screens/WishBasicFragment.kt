package com.hyeeyoung.wishboard.view.wish.item.screens

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentWishBinding
import com.hyeeyoung.wishboard.model.common.ProcessStatus
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.model.wish.WishItemStatus
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.safeLet
import com.hyeeyoung.wishboard.view.folder.FolderListDialogListener
import com.hyeeyoung.wishboard.view.folder.screens.FolderListBottomDialogFragment
import com.hyeeyoung.wishboard.view.noti.screens.NotiSettingBottomDialogFragment
import com.hyeeyoung.wishboard.viewmodel.WishItemRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class WishBasicFragment : Fragment() {
    private lateinit var binding: FragmentWishBinding
    private val viewModel: WishItemRegistrationViewModel by hiltNavGraphViewModels(R.id.wish_item_registration_nav_graph)

    /** 아이템 수정 여부에 따라 아이템을 update 또는 upload를 진행 (등록 및 수정 시 동일한 뷰를 사용하고 있기 때문) */
    private var isEditMode = false
    private var folder: FolderItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            (it[ARG_IS_EDIT_MODE] as? Boolean)?.let { isEditable ->
                isEditMode = isEditable
            }
            (it[ARG_WISH_ITEM] as? WishItem)?.let { item ->
                viewModel.setWishItem(item)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@WishBasicFragment

        initializeView()
        addListeners()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        // TODO need refactoring
        binding.itemUploadTitle.text =
            context?.getString(
                if (isEditMode) {
                    R.string.wish_basic_item_edit_title
                } else {
                    R.string.wish_basic_item_add_title
                }
            )

        Glide.with(binding.itemImage).load(viewModel.getWishItem()?.imageUrl)
            .into(binding.itemImage)
    }

    private fun addListeners() {
        binding.save.setOnClickListener {
            if (isValidItemUrl() == false) return@setOnClickListener

            lifecycleScope.launch {
                when (isEditMode) {
                    false -> viewModel.uploadWishItemByBasics()
                    true -> viewModel.updateWishItem()
                }
            }
        }
        binding.addPhoto.setOnClickListener {
            requestStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        binding.folderContainer.setOnClickListener {
            showFolderListDialog()
        }
        binding.notiContainer.setOnClickListener {
            NotiSettingBottomDialogFragment(viewModel).show(
                parentFragmentManager,
                "NotiSettingDialog"
            )
        }
    }

    private fun addObservers() {
        viewModel.isCompleteUpload().observe(viewLifecycleOwner) { isCompleted ->
            when (isCompleted) {
                true -> {
                    if (isEditMode) {
                        CustomSnackbar.make(
                            binding.layout,
                            getString(R.string.wish_item_update_snackbar_text)
                        ).show()
                        moveToPrevious(WishItemStatus.MODIFIED, viewModel.getWishItem())
                    } else {
                        CustomSnackbar.make(
                            binding.layout,
                            getString(R.string.wish_item_registration_snackbar_text)
                        ).show()
                        moveToPrevious(WishItemStatus.ADDED, null)
                    }
                }
            }
        }

        viewModel.getRegistrationStatus().observe(viewLifecycleOwner) {
            when (it) {
                ProcessStatus.IDLE -> {
                    binding.loadingLottie.visibility = View.GONE
                }
                ProcessStatus.IN_PROGRESS -> {
                    binding.loadingLottie.visibility = View.VISIBLE
                    binding.loadingLottie.playAnimation()
                }
            }
        }

        // 갤러리에서 선택한 이미지 전달받기
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(
            ARG_IMAGE_INFO
        )?.observe(viewLifecycleOwner) {
            safeLet(
                it[ARG_IMAGE_URI] as? Uri, it[ARG_IMAGE_FILE] as? File
            ) { imageUri, imageFile ->
                Glide.with(binding.itemImage).load(imageUri).into(binding.itemImage)
                viewModel.setSelectedGalleryImage(imageUri, imageFile)
            }
        }
    }

    private fun showFolderListDialog() {
        val folderId = folder?.id ?: viewModel.getWishItem()?.folderId
        val dialog = FolderListBottomDialogFragment(folderId).apply {
            setListener(object : FolderListDialogListener {
                override fun onButtonClicked(folder: FolderItem) {
                    this@WishBasicFragment.folder = folder // TODO need refactoring
                    viewModel.setFolderItem(folder)
                    dismiss()
                }
            })
        }
        dialog.show(parentFragmentManager, "FolderListDialog")
    }

    /** 아이템 추가 또는 수정에 성공한 경우, UI 업데이트를 위해 변경된 아이템 정보를 이전 프래그먼트로 전달 */
    private fun moveToPrevious(itemStatus: WishItemStatus, wishItem: WishItem?) {
        val navController = findNavController()
        navController.previousBackStackEntry?.savedStateHandle?.set(
            ARG_WISH_ITEM_INFO, bundleOf(
                ARG_ITEM_STATUS to itemStatus,
                ARG_WISH_ITEM to wishItem,
            )
        )
        navController.popBackStack()
    }

    private val requestStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                findNavController().navigateSafe(R.id.action_wish_to_gallery_image)
            }
        }

    // 아이템 등록 전 url 유효성을 검증, TODO need refactoring
    private fun isValidItemUrl(): Boolean? {
        val url = viewModel.getRefinedItemUrl(binding.itemUrl.text.toString()) ?: return null

        return if (Patterns.WEB_URL.matcher(url).matches()) {
            true
        } else {
            CustomSnackbar.make(
                binding.layout,
                requireContext().getString(R.string.wish_basic_invalid_site_snackbar_text)
            ).show()
            false
        }
    }

    companion object {
        private const val TAG = "WishBasicFragment"
        private const val ARG_WISH_ITEM = "wishItem"
        private const val ARG_IS_EDIT_MODE = "isEditMode"
        private const val ARG_IMAGE_INFO = "imageInfo"
        private const val ARG_IMAGE_URI = "imageUri"
        private const val ARG_IMAGE_FILE = "imageFile"
        private const val ARG_ITEM_STATUS = "itemStatus"
        private const val ARG_WISH_ITEM_INFO = "wishItemInfo"
    }
}