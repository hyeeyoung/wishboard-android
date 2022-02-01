package com.hyeeyoung.wishboard.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.model.folder.FolderListViewType
import com.hyeeyoung.wishboard.repository.folder.FolderRepository
import com.hyeeyoung.wishboard.util.prefs
import com.hyeeyoung.wishboard.view.folder.adapters.FolderListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val application: Application,
    private val folderRepository: FolderRepository,
) : ViewModel() {
    private val token = prefs?.getUserToken()

    private val folderListAdapter =
        FolderListAdapter(application, FolderListViewType.SQUARE_VIEW_TYPE)
    private val folderListSummaryAdapter =
        FolderListAdapter(application, FolderListViewType.HORIZONTAL_VIEW_TYPE)
    private var isCompleteDeletion = MutableLiveData<Boolean>()

    fun fetchFolderList() {
        if (token == null) return
        viewModelScope.launch {
            folderListAdapter.setData(folderRepository.fetchFolderList(token) ?: return@launch)
        }
    }

    fun fetchFolderListSummary() {
        if (token == null) return
        viewModelScope.launch {
            folderListSummaryAdapter.setData(
                folderRepository.fetchFolderListSummary(token) ?: return@launch
            )
        }
    }

    fun deleteFolder(folder: FolderItem?, position: Int?) {
        if (token == null || folder?.id == null) return // TODO 삭제 실패 예외처리 필요
        viewModelScope.launch {
            isCompleteDeletion.value = folderRepository.deleteFolder(token, folder.id)
        }
        folderListAdapter.deleteData(position ?: return, folder)
    }

    fun resetCompleteDeletion() {
        isCompleteDeletion.value = false
    }

    fun getFolderListAdapter(): FolderListAdapter = folderListAdapter
    fun getFolderListSummaryAdapter(): FolderListAdapter = folderListSummaryAdapter
    fun getIsCompleteDeletion(): LiveData<Boolean> = isCompleteDeletion

    companion object {
        private const val TAG = "FolderViewModel"
    }
}