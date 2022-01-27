package com.hyeeyoung.wishboard.view.wish.item.screens

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentWishBinding
import com.hyeeyoung.wishboard.model.wish.WishItem
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.viewmodel.WishItemRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WishFragment : Fragment() { // TODO rename class name
    private lateinit var binding: FragmentWishBinding
    private val viewModel: WishItemRegistrationViewModel by hiltNavGraphViewModels(R.id.wish_item_registration_nav_graph)

    /** 아이템 수정 여부에 따라 아이템을 update 또는 upload를 진행 (등록 및 수정 시 동일한 뷰를 사용하고 있기 때문) */
    private var isEditMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        arguments?.let {
            (it[ARG_IS_EDIT_MODE] as? Boolean)?.let { isEditable ->
                isEditMode = isEditable
            }
            (it[ARG_WISH_ITEM] as? WishItem)?.let { item ->
                viewModel.setWishItemInfo(item)
                item.image?.let { image ->
                    Glide.with(requireContext()).load(image).into(binding.itemImage)
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

        binding.itemImageLayout.setOnClickListener {
            requestStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun addObservers() {
        viewModel.isCompleteUpload().observe(viewLifecycleOwner) { isCompleted ->
            when (isCompleted) {
                true -> {
                    init()
                    viewModel.setCompletedUpload(null)
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

    companion object {
        private const val TAG = "WishFragment"
        private const val ARG_WISH_ITEM = "wishItem"
        private const val ARG_IS_EDIT_MODE = "isEditMode"
    }
}