package com.hyeeyoung.wishboard.view.folder.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentFolderListBinding
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.util.ImageLoader
import com.hyeeyoung.wishboard.util.loadImage
import com.hyeeyoung.wishboard.view.folder.adapters.FolderListAdapter
import com.hyeeyoung.wishboard.viewmodel.WishItemRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderListFragment : Fragment(), FolderListAdapter.OnItemClickListener, ImageLoader {
    private lateinit var binding: FragmentFolderListBinding
    private val viewModel: WishItemRegistrationViewModel by hiltNavGraphViewModels(R.id.wish_item_registration_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFolderListBinding.inflate(inflater, container, false)

        initializeView()

        return binding.root
    }

    private fun initializeView() {
        val adapter = viewModel.getFolderListAdapter()
        adapter.setOnItemClickListener(this)
        adapter.setImageLoader(this)
        binding.folderList.adapter = adapter
        binding.folderList.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onItemClick(item: FolderItem) {
        viewModel.setFolderItem(item)
        findNavController().popBackStack()
    }

    override fun loadImage(imageUrl: String, imageView: ImageView) {
        loadImage(lifecycleScope, requireContext(), imageUrl, imageView)
    }

    companion object {
        private const val TAG = "FolderListFragment"
    }
}