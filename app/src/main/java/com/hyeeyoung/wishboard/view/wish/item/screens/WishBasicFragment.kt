package com.hyeeyoung.wishboard.view.wish.item.screens

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentWishBinding
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.util.ImageLoader
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.loadImage
import com.hyeeyoung.wishboard.viewmodel.WishItemRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WishBasicFragment : Fragment(), ImageLoader {
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
                    loadImage(item.image ?: return@let, binding.itemImage)
                }
            }
        }

        addListeners()
        addObservers()

        return binding.root
    }

    private fun init() {
        viewModel.setSelectedGalleryImageUri(null)
        binding.itemImage.setImageDrawable(null)
        binding.itemName.setText("")
        binding.itemPrice.setText("")
        binding.itemUrl.setText("")
        binding.itemMemo.setText("")
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
        binding.itemImageLayout.setOnClickListener { // TODO itemImageContainer로 변경
            requestStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        binding.folderContainer.setOnClickListener {
            findNavController().navigateSafe(R.id.action_wish_to_folder_list)
        }
    }

    private fun addObservers() {
        viewModel.isCompleteUpload().observe(viewLifecycleOwner) { isCompleted ->
            when (isCompleted) {
                true -> {
                    init()
                    val navController = findNavController()
                    if (isEditMode) {
                        Toast.makeText(
                            context,
                            getString(R.string.wish_item_update_toast_text),
                            Toast.LENGTH_SHORT
                        ).show()
                        // 아이템 수정 성공한 경우, 상세조회 UI 업데이트를 위해 변경된 아이템 정보를 DetailFragment로 전달
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            ARG_WISH_ITEM, viewModel.getWishItem()
                        )
                    } else {
                        Toast.makeText(
                            context,
                            getString(R.string.wish_item_upload_toast_text),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    navController.popBackStack()
                }
            }
        }

        viewModel.getSelectedGalleryImageUri().observe(viewLifecycleOwner) { uri ->
            if (uri == null) {
                return@observe
            } else {
                Glide.with(requireContext()).load(uri).into(binding.itemImage)
            }
        }
    }

    private val requestStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                viewModel.clearGalleryImageUris()
                findNavController().navigateSafe(R.id.action_wish_to_gallery_image)
            }
        }

    override fun loadImage(imageUrl: String, imageView: ImageView) {
        loadImage(lifecycleScope, imageUrl, imageView)
    }

    companion object {
        private const val TAG = "WishBasicFragment"
        private const val ARG_WISH_ITEM = "wishItem"
        private const val ARG_IS_EDIT_MODE = "isEditMode"
    }
}