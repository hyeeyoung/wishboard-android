package com.hyeeyoung.wishboard.view.folder.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentFolderBinding
import com.hyeeyoung.wishboard.model.common.DialogButtonReplyType
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.model.folder.FolderMoreDialogButtonReplyType
import com.hyeeyoung.wishboard.util.ImageLoader
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.loadImage
import com.hyeeyoung.wishboard.view.common.screens.DialogListener
import com.hyeeyoung.wishboard.view.common.screens.TwoButtonDialogFragment
import com.hyeeyoung.wishboard.view.folder.adapters.FolderListAdapter
import com.hyeeyoung.wishboard.viewmodel.FolderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderFragment : Fragment(), FolderListAdapter.OnItemClickListener,
    FolderListAdapter.OnFolderMoreDialogListener, ImageLoader {
    private lateinit var binding: FragmentFolderBinding
    private val viewModel: FolderViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchFolderList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFolderBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        initializeView()
        addListeners()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        val adapter = viewModel.getFolderListAdapter()
        adapter.setOnItemClickListener(this)
        adapter.setOnFolderMoreDialogListener(this)
        adapter.setImageLoader(this)

        binding.folderList.apply {
            this.adapter = adapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            itemAnimator = null
            setItemViewCacheSize(20)
        }
    }

    private fun addListeners() {
        binding.newFolder.setOnClickListener {
            findNavController().navigateSafe(R.id.action_folder_to_folder_add_dialog)
        }
    }

    private fun addObservers() {
        viewModel.getIsCompleteDeletion().observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.folder_delete_toast_text),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetCompleteDeletion()
            }
        }
    }

    override fun onItemClick(item: FolderItem) {
        findNavController().navigateSafe(
            R.id.action_folder_to_folder_detail,
            bundleOf(ARG_FOLDER_ITEM to item)
        )
    }

    /** 폴더 아이템의 더보기 버튼 클릭 시 폴더 더보기 다이얼로그 띄우기 */
    override fun onItemMoreButtonClick(position: Int, item: FolderItem) {
        showFolderMoreDialog(position, item)
    }

    override fun loadImage(imageUrl: String, imageView: ImageView) {
        loadImage(lifecycleScope, imageUrl, imageView)
    }

    /** 폴더 더보기 다이얼로그 */
    private fun showFolderMoreDialog(position: Int, folderItem: FolderItem) {
        val dialog = FolderMoreDialogFragment().apply {
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: String) {
                    when (clicked) {
                        FolderMoreDialogButtonReplyType.UPDATE.name -> {
                            findNavController().navigateSafe(
                                R.id.action_folder_to_folder_add_dialog, bundleOf(
                                    ARG_FOLDER_ITEM to folderItem,
                                    ARG_FOLDER_POSITION to position
                                )
                            )
                        }
                        FolderMoreDialogButtonReplyType.DELETE.name -> {
                            showFolderDeleteDialog(position, folderItem)
                        }
                    }
                    dismiss()
                }
            })
        }
        dialog.show(parentFragmentManager, "FolderMoreDialog")
    }

    /** 폴더 삭제 다이얼로그 */
    private fun showFolderDeleteDialog(position: Int, folderItem: FolderItem) {
        val dialog = TwoButtonDialogFragment(
            getString(R.string.folder_delete_dialog_title),
            getString(R.string.folder_delete_dialog_detail),
            getString(R.string.delete)
        ).apply {
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: String) {
                    if (clicked == DialogButtonReplyType.YES.name) {
                        viewModel.deleteFolder(folderItem, position)
                    }
                    dismiss()
                }
            })
        }
        dialog.show(parentFragmentManager, "FolderDeleteDialog")
    }

    companion object {
        private const val TAG = "FolderFragment"
        private const val ARG_FOLDER_ITEM = "folderItem"
        private const val ARG_FOLDER_POSITION = "folderPosition"
    }
}