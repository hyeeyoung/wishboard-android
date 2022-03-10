package com.hyeeyoung.wishboard.view.wish.item.screens

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.model.wish.WishItemStatus
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.safeLet
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

    /** 해당 화면을 방문 여부를 구분하기 위한 변수 */
    private var isVisited = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@WishBasicFragment

        // 갤러리 이미지 선택 화면으로 전환 -> 해당 화면 복귀할 경우, 생명주기상 onCreateView를 재호출되고,
        // if문 내 코드를 실행하면서 입력된 정보가 reset되는 문제가 있음
        // 입력된 데이터가 reset되는 것을 방지하기 위해 방문한 적이 있는 경우 아래 코드를 실행하지 않도록 함
        if (!isVisited) {
            isVisited = true
            arguments?.let {
                (it[ARG_IS_EDIT_MODE] as? Boolean)?.let { isEditable ->
                    isEditMode = isEditable
                }
                (it[ARG_WISH_ITEM] as? WishItem)?.let { item ->
                    viewModel.setWishItem(item)
                    Glide.with(binding.itemImage).load(item.imageUrl).into(binding.itemImage)
                }
            }
        }

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
    }

    private fun addListeners() {
        binding.save.setOnClickListener {
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
        binding.editPhoto.setOnClickListener {
            requestStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        binding.folderContainer.setOnClickListener {
            findNavController().navigateSafe(R.id.action_wish_to_folder_list)
        }
        binding.notiContainer.setOnClickListener {
            findNavController().navigateSafe(R.id.action_wish_to_noti_setting)
        }
    }

    private fun addObservers() {
        viewModel.isCompleteUpload().observe(viewLifecycleOwner) { isCompleted ->
            when (isCompleted) {
                true -> {
                    if (isEditMode) {
                        Toast.makeText(
                            context,
                            getString(R.string.wish_item_update_toast_text),
                            Toast.LENGTH_SHORT
                        ).show()
                        moveToPrevious(WishItemStatus.MODIFIED, viewModel.getWishItem())
                    } else {
                        Toast.makeText(
                            context,
                            getString(R.string.wish_item_upload_toast_text),
                            Toast.LENGTH_SHORT
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
                Glide.with(requireContext()).load(imageUri).into(binding.itemImage)
                viewModel.setSelectedGalleryImage(imageUri, imageFile)
            }
        }
    }

    /** 아이템 추가 또는 수정에 성공한 경우, UI 업데이트를 위해 변경된 아이템 정보를 이전 프래그먼트로 전달 */
    private fun moveToPrevious(itemStatus: WishItemStatus, wishItem: WishItem?) {
        val navController = findNavController()
        navController.previousBackStackEntry?.savedStateHandle?.set(
            ARG_WISH_ITEM_INFO, bundleOf(
            ARG_ITEM_STATUS to itemStatus,
            ARG_WISH_ITEM to wishItem,
        ))
        navController.popBackStack()
    }

    private val requestStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                viewModel.clearGalleryImageUris()
                findNavController().navigateSafe(R.id.action_wish_to_gallery_image)
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