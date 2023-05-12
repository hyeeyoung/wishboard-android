package com.hyeeyoung.wishboard.presentation.common.screens

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.BuildConfig.APPLICATION_ID
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentGalleryImageBinding
import com.hyeeyoung.wishboard.presentation.common.adapters.GalleryPagingAdapter
import com.hyeeyoung.wishboard.presentation.common.viewmodels.GalleryViewModel
import com.hyeeyoung.wishboard.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GalleryImageFragment :
    BaseFragment<FragmentGalleryImageBinding>(R.layout.fragment_gallery_image) {
    private lateinit var adapter: GalleryPagingAdapter
    private val viewModel: GalleryViewModel by viewModels()
    private var photoUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeView()
        addListeners()
        addCollectors()
    }

    private fun initializeView() {
        adapter = GalleryPagingAdapter(::onItemClick)
        binding.imageList.adapter = adapter
    }

    fun onItemClick(imageUri: Uri) {
        moveToPrevious(imageUri)
    }

    private fun addListeners() {
        binding.camera.setOnClickListener {
            requestCamera.launch(Manifest.permission.CAMERA)
        }
    }

    private fun addCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.imageList.collectLatest { image ->
                adapter.submitData(image)
            }
        }
    }

    private fun takePicture() {
        val imageFile = viewModel.createCameraImageFile() ?: return
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "$APPLICATION_ID.fileprovider",
            imageFile
        )
        takePicture.launch(photoUri)
    }

    private val requestCamera =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                takePicture()
            }
        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success)
                photoUri?.let { moveToPrevious(it) }
        }

    private fun moveToPrevious(galleryImageUri: Uri) {
        val navController = findNavController()
        navController.previousBackStackEntry?.savedStateHandle?.set(
            ARG_IMAGE_URI,
            galleryImageUri.toString()
        )
        navController.popBackStack()
    }

    companion object {
        private const val ARG_IMAGE_URI = "imageUri"
    }
}