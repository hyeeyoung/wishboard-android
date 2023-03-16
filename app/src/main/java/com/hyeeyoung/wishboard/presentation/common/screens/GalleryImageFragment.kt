package com.hyeeyoung.wishboard.presentation.common.screens

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.BuildConfig.APPLICATION_ID
import com.hyeeyoung.wishboard.databinding.FragmentGalleryImageBinding
import com.hyeeyoung.wishboard.presentation.common.adapters.GalleryPagingAdapter
import com.hyeeyoung.wishboard.presentation.common.viewmodels.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class GalleryImageFragment : Fragment() {
    private lateinit var binding: FragmentGalleryImageBinding
    private lateinit var adapter: GalleryPagingAdapter
    private val viewModel: GalleryViewModel by viewModels()
    private var photoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGalleryImageBinding.inflate(inflater, container, false)

        initializeView()
        addListeners()
        addCollectors()

        return binding.root
    }

    private fun initializeView() {
        adapter = GalleryPagingAdapter(::onItemClick)
        binding.imageList.adapter = adapter
    }

    fun onItemClick(imageUri: Uri) {
        moveToPrevious(imageUri, viewModel.copyImageToInternalStorage(imageUri))
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
            if (success) {
                photoUri?.let {
                    viewModel.setSelectedGalleryImageUri(it)
                    moveToPrevious(it, viewModel.getImageFile())
                }
            }
        }

    private fun moveToPrevious(galleryImageUri: Uri, cameraImageFile: File?) {
        val navController = findNavController()
        navController.previousBackStackEntry?.savedStateHandle?.set(
            ARG_IMAGE_INFO, bundleOf(
                ARG_IMAGE_URI to galleryImageUri.toString(),
                ARG_IMAGE_FILE to cameraImageFile
            )
        )
        navController.popBackStack()
    }

    companion object {
        private const val ARG_IMAGE_INFO = "imageInfo"
        private const val ARG_IMAGE_URI = "imageUri"
        private const val ARG_IMAGE_FILE = "imageFile"
    }
}