package com.hyeeyoung.wishboard.view.common.screens

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hyeeyoung.wishboard.BuildConfig.APPLICATION_ID
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentGalleryImageBinding
import com.hyeeyoung.wishboard.view.common.adapters.GalleryPagingAdapter
import com.hyeeyoung.wishboard.viewmodel.WishItemRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GalleryImageFragment : Fragment(),
    GalleryPagingAdapter.OnItemClickListener {
    private lateinit var binding: FragmentGalleryImageBinding
    private lateinit var adapter: GalleryPagingAdapter
    private val viewModel: WishItemRegistrationViewModel by hiltNavGraphViewModels(R.id.wish_item_registration_nav_graph)
    private var photoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGalleryImageBinding.inflate(inflater, container, false)

        initializeView()
        addListeners()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        adapter = GalleryPagingAdapter(requireContext())
        adapter.setOnItemClickListener(this)

        binding.imageList.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.imageList.adapter = adapter

        viewModel.fetchGalleryImageUris(requireActivity().contentResolver)
    }

    override fun onItemClick(imageUri: Uri) {
        viewModel.setSelectedGalleryImageUri(imageUri)
        findNavController().popBackStack()
    }

    private fun addListeners() {
        binding.camera.setOnClickListener {
            requestCamera.launch(Manifest.permission.CAMERA)
        }
    }

    private fun addObservers() {
        viewModel.getGalleryImageUris().observe(viewLifecycleOwner) {
            if (it == null) return@observe
            lifecycleScope.launch {
                adapter.submitData(it)
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
                    findNavController().popBackStack()
                }
            }
        }

    companion object {
        private const val TAG = "ImageSelectionActivity"
    }
}