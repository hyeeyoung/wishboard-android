package com.hyeeyoung.wishboard.presentation.folder.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentFolderBinding
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.presentation.folder.types.FolderMoreDialogButtonReplyType
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import com.hyeeyoung.wishboard.util.DialogListener
import com.hyeeyoung.wishboard.presentation.common.screens.TwoButtonDialogFragment
import com.hyeeyoung.wishboard.presentation.folder.FolderListAdapter
import com.hyeeyoung.wishboard.presentation.folder.FolderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderFragment : Fragment(), FolderListAdapter.OnItemClickListener,
    FolderListAdapter.OnFolderMoreDialogListener {
    private lateinit var binding: FragmentFolderBinding
    private val viewModel: FolderViewModel by activityViewModels()
    private var folderAddDialog: FolderAddDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFolderBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initializeView()
        addListeners()
        addObservers()

        return binding.root
    }

    private fun initializeView() {
        val adapter = viewModel.getFolderListAdapter()
        adapter.setOnItemClickListener(this)
        adapter.setOnFolderMoreDialogListener(this)

        binding.folderList.apply {
            this.adapter = adapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            itemAnimator = null
            setItemViewCacheSize(20)
        }
    }

    private fun addListeners() {
        binding.newFolder.setOnClickListener {
            showFolderUploadDialog()
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchFolderList()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun addObservers() {
        viewModel.getIsCompleteDeletion().observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                CustomSnackbar.make(binding.layout, getString(R.string.folder_delete_snackbar_text)).show()
                viewModel.resetCompleteDeletion()
            }
        }
        viewModel.getIsCompleteUpload().observe(viewLifecycleOwner) { isComplete ->
            if (isComplete == true) {
                folderAddDialog?.dismiss()
                val toastMessageRes = when (viewModel.getEditMode()) {
                    true -> R.string.folder_name_update_snackbar_text
                    else -> R.string.folder_add_snackbar_text
                }
                CustomSnackbar.make(binding.layout, getString(toastMessageRes)).show()
            }
            return@observe
        }
    }

    override fun onItemClick(item: FolderItem) {
        findNavController().navigateSafe(
            R.id.action_folder_to_folder_detail,
            bundleOf(ARG_FOLDER_ITEM to item)
        )
    }

    /** 폴더 아이템의 더보기 버튼 클릭 시 폴더 더보기 다이얼로그 띄우기 */
    override fun onItemMoreButtonClick(item: FolderItem) {
        showFolderMoreDialog(item)
    }

    /** 폴더 더보기 다이얼로그 */
    private fun showFolderMoreDialog(folderItem: FolderItem) {
        val dialog = FolderMoreDialogFragment().apply {
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: String) {
                    when (clicked) {
                        FolderMoreDialogButtonReplyType.UPDATE.name -> {
                            showFolderUploadDialog(folderItem)
                        }
                        FolderMoreDialogButtonReplyType.DELETE.name -> {
                            showFolderDeleteDialog(folderItem)
                        }
                    }
                    dismiss()
                }
            })
        }
        dialog.show(parentFragmentManager, "FolderMoreDialog")
    }

    /** 폴더 업로드 다이얼로그 */
    private fun showFolderUploadDialog(folderItem: FolderItem? = null) {
        viewModel.setFolderInfo(folderItem)
        viewModel.setEditMode(folderItem != null)

        folderAddDialog = FolderAddDialogFragment().apply {
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: String) {
                    when (clicked) {
                        DialogButtonReplyType.YES.name -> viewModel.uploadFolder()
                        else -> dismiss()
                    }
                }
            })
        }
        folderAddDialog?.show(parentFragmentManager, "FolderUploadDialog")
    }

    /** 폴더 삭제 다이얼로그 */
    private fun showFolderDeleteDialog(folderItem: FolderItem) {
        val dialog = TwoButtonDialogFragment(
            getString(R.string.folder_delete),
            getString(R.string.folder_delete_dialog_detail),
            getString(R.string.delete),
            getString(R.string.cancel),

        ).apply {
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: String) {
                    if (clicked == DialogButtonReplyType.YES.name) {
                        viewModel.deleteFolder(folderItem)
                    }
                    dismiss()
                }
            })
        }
        dialog.show(parentFragmentManager, "FolderDeleteDialog")
    }

    companion object {
        private const val ARG_FOLDER_ITEM = "folderItem"
    }
}