package com.hyeeyoung.wishboard.presentation.folder.screens

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.databinding.FragmentFolderBinding
import com.hyeeyoung.wishboard.designsystem.component.CustomSnackbar
import com.hyeeyoung.wishboard.presentation.base.screen.NetworkFragment
import com.hyeeyoung.wishboard.presentation.common.screens.TwoButtonDialogFragment
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.presentation.folder.FolderListAdapter
import com.hyeeyoung.wishboard.presentation.folder.FolderViewModel
import com.hyeeyoung.wishboard.presentation.folder.types.FolderListViewType
import com.hyeeyoung.wishboard.presentation.folder.types.TwoOptionDialogReplyType
import com.hyeeyoung.wishboard.util.DialogListener
import com.hyeeyoung.wishboard.util.UiState
import com.hyeeyoung.wishboard.util.extension.navigateSafe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderFragment : NetworkFragment<FragmentFolderBinding>(R.layout.fragment_folder),
    FolderListAdapter.OnItemClickListener,
    FolderListAdapter.OnFolderMoreDialogListener {
    private val viewModel: FolderViewModel by activityViewModels()
    private lateinit var folderListAdapter: FolderListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchFolderList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initializeView()
        addListeners()
        addObservers()
        collectData()
    }

    private fun initializeView() {
        folderListAdapter = FolderListAdapter(FolderListViewType.VERTICAL_VIEW_TYPE).apply {
            setOnItemClickListener(this@FolderFragment)
            setOnFolderMoreDialogListener(this@FolderFragment)
        }

        binding.folderList.apply {
            this.adapter = folderListAdapter
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
        viewModel.folderDeleteState.observe(viewLifecycleOwner) { deleteState ->
            when (deleteState) {
                is UiState.Success -> {
                    CustomSnackbar.make(
                        binding.layout,
                        getString(R.string.folder_delete_snackbar_text)
                    ).show()
                    folderListAdapter.deleteData(deleteState.data)
                    viewModel.resetCompleteDeletion()
                }

                else -> {}
            }
        }
    }

    private fun collectData() {
        viewModel.folderFetchState.observe(viewLifecycleOwner) { fetchState ->
            when (fetchState) {
                is UiState.Success -> {
                    folderListAdapter.setData(items = fetchState.data)
                }

                else -> {}
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
    override fun onItemMoreButtonClick(item: FolderItem) {
        showFolderMoreDialog(item)
    }

    /** 폴더 더보기 다이얼로그 */
    private fun showFolderMoreDialog(folderItem: FolderItem) {
        val dialog = TwoOptionDialogFragment.newInstance(
            topOption = getString(R.string.folder_name_edit),
            bottomOption = getString(R.string.folder_delete)
        ).apply {
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: String) {
                    when (clicked) {
                        TwoOptionDialogReplyType.TOP_OPTION.name -> {
                            showFolderUploadDialog(folderItem)
                        }

                        TwoOptionDialogReplyType.BOTTOM_OPTION.name -> {
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
        FolderUploadBottomDialogFragment.newInstance(folderItem).apply {
            setListener(object : FolderUploadBottomDialogFragment.OnFolderUploadListener {
                override fun onSuccessUpload(newFolder: FolderItem, oldFolder: FolderItem?) {
                    if (oldFolder != null) {
                        folderListAdapter.updateData(oldFolder, newFolder)
                        CustomSnackbar.make(
                            this@FolderFragment.binding.layout,
                            getString(R.string.folder_name_update_snackbar_text)
                        ).show()
                    } else {
                        viewModel.increaseFolderCount()
                        folderListAdapter.addData(newFolder)
                        CustomSnackbar.make(
                            this@FolderFragment.binding.layout,
                            getString(R.string.folder_add_snackbar_text)
                        ).show()
                    }
                }

                override fun onFailureUpload() {}
            })
        }.show(parentFragmentManager, "FolderUploadDialog")
    }

    /** 폴더 삭제 다이얼로그 */
    private fun showFolderDeleteDialog(folderItem: FolderItem) {
        val dialog = TwoButtonDialogFragment.newInstance(
            title = getString(R.string.folder_delete),
            description = getString(R.string.folder_delete_dialog_detail),
            yesValue = getString(R.string.delete),
            noValue = getString(R.string.cancel)
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
        const val ARG_FOLDER_ITEM = "folderItem"
    }
}