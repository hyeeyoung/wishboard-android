package com.hyeeyoung.wishboard.presentation.wishitem.screens

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.databinding.FragmentWishBinding
import com.hyeeyoung.wishboard.designsystem.component.CustomSnackbar
import com.hyeeyoung.wishboard.domain.model.WishItemDetail
import com.hyeeyoung.wishboard.presentation.common.screens.OneButtonDialogFragment
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.presentation.folder.screens.FolderListBottomDialogFragment
import com.hyeeyoung.wishboard.presentation.noti.screens.NotiSettingBottomDialogFragment
import com.hyeeyoung.wishboard.presentation.wishitem.WishItemStatus
import com.hyeeyoung.wishboard.presentation.wishitem.viewmodels.WishItemRegistrationViewModel
import com.hyeeyoung.wishboard.util.BaseFragment
import com.hyeeyoung.wishboard.util.DialogListener
import com.hyeeyoung.wishboard.util.FolderListDialogListener
import com.hyeeyoung.wishboard.util.extension.getParcelableValue
import com.hyeeyoung.wishboard.util.extension.requestCamera
import com.hyeeyoung.wishboard.util.extension.requestSelectPicture
import com.hyeeyoung.wishboard.util.extension.showPhotoDialog
import com.hyeeyoung.wishboard.util.extension.takePicture
import com.hyeeyoung.wishboard.util.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WishBasicFragment : BaseFragment<FragmentWishBinding>(R.layout.fragment_wish) {
    private val viewModel: WishItemRegistrationViewModel by hiltNavGraphViewModels(R.id.wish_item_registration_nav_graph)

    /** 아이템 수정 여부에 따라 아이템을 update 또는 upload를 진행 (등록 및 수정 시 동일한 뷰를 사용하고 있기 때문) */
    private var isEditMode = false
    private var folder: FolderItem? = null

    private lateinit var notiSettingBottomDialog: BottomSheetDialogFragment
    private val shopLinkInputDialog = ShopLinkInputBottomDialogFragment()
    private var photoUri: Uri? = null

    private val requestSelectPicture = requestSelectPicture { uri -> selectAndLoadImage(uri) }
    private val requestCamera = requestCamera {
        photoUri = viewModel.createCameraImageUri()
        takePicture.launch(photoUri)
    }
    private val takePicture = takePicture {
        selectAndLoadImage(photoUri ?: return@takePicture)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            it.getBoolean(ARG_IS_EDIT_MODE).let { isEditable ->
                isEditMode = isEditable
            }
            (it.getParcelableValue(
                ARG_WISH_ITEM_DETAIL,
                WishItemDetail::class.java
            ))?.let { detail ->
                viewModel.wishItemDetail = detail
            }
        }

        notiSettingBottomDialog = NotiSettingBottomDialogFragment(viewModel) // TODO refactoring
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initializeView()
        addListeners()
        addObservers()
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

        viewModel.wishItemDetail?.let {
            binding.itemImage.load(it.image)
        }
    }

    private fun addListeners() {
        binding.save.setOnClickListener {
            viewModel.uploadWishItemByBasics(isEditMode)
        }
        binding.addPhoto.setOnSingleClickListener {
            showPhotoDialog(requestCamera, requestSelectPicture)
        }
        binding.folderContainer.setOnClickListener {
            showFolderListDialog()
        }
        binding.notiContainer.setOnClickListener {
            if (notiSettingBottomDialog.isAdded) return@setOnClickListener
            notiSettingBottomDialog.show(parentFragmentManager, "NotiSettingDialog")
        }
        binding.itemUrlContainer.setOnClickListener {
            if (shopLinkInputDialog.isAdded) return@setOnClickListener
            shopLinkInputDialog.show(parentFragmentManager, "ShopLinkInputDialog")
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
                        moveToPrevious(WishItemStatus.MODIFIED)
                    } else {
                        CustomSnackbar.make(
                            binding.layout,
                            getString(R.string.wish_item_registration_snackbar_text)
                        ).show()
                        moveToPrevious(WishItemStatus.ADDED)
                    }
                }

                else -> {}
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

                else -> {}
            }
        }

        // 파싱으로 가져온 이미지
        viewModel.getItemImage().observe(viewLifecycleOwner) {
            // TODO 정규 표현식으로 바꿔서 조건 하나로 만들기
            if (it?.contains("http://") == true || it?.contains("https://") == true) {
                binding.itemImage.load(it)
            }
        }

        viewModel.isShownItemNonUpdateDialog.observe(viewLifecycleOwner) { isShown ->
            if (isShown == true) showItemNonUpdateDialog()
        }
    }

    private fun showItemNonUpdateDialog() {
        OneButtonDialogFragment.newInstance(
            title = getString(R.string.item_non_update_dialog_title),
            description = getString(R.string.item_non_update_dialog_description)
        ).apply {
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: String) {
                    if (clicked == DialogButtonReplyType.YES.name) {
                        dismiss()
                    }
                }
            })
        }.show(parentFragmentManager, "ItemNonUpdateDialog")
    }

    private fun showFolderListDialog() {
        val folderId = folder?.id ?: viewModel.wishItemDetail?.folderId
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
    private fun moveToPrevious(itemStatus: WishItemStatus) {
        val navController = findNavController()
        navController.previousBackStackEntry?.savedStateHandle?.set(
            ARG_WISH_ITEM_INFO, bundleOf(ARG_ITEM_STATUS to itemStatus.name)
        )
        navController.popBackStack()
    }

    private fun selectAndLoadImage(uri: Uri) {
        viewModel.setSelectedGalleryImage(uri)
        viewModel.removeWishItemImage()
        binding.itemImage.load(uri)
    }

    companion object {
        private const val ARG_IS_EDIT_MODE = "isEditMode"
        private const val ARG_ITEM_STATUS = "itemStatus"
        private const val ARG_WISH_ITEM_INFO = "wishItemInfo"
        private const val ARG_WISH_ITEM_DETAIL = "wishItemDetail"
    }
}