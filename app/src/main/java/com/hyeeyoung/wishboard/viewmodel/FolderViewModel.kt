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
    private var folderName = MutableLiveData<String>()
    private var folderItem: FolderItem? = null
    private var isCompleteAddition = MutableLiveData<Boolean>()
    private var isCompleteUpdate = MutableLiveData<Boolean>()
    private var isCompleteDeletion = MutableLiveData<Boolean>()
    private var isEditMode = MutableLiveData<Boolean>()

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

    fun createNewFolder() {
        val folderName = folderName.value?.trim()
        if (token == null || folderName == null) return
        val folderInfo = FolderItem(name = folderName)

        viewModelScope.launch {
            folderItem = folderInfo
            val folderId = folderRepository.createNewFolder(token, folderInfo) ?: return@launch
            folderListAdapter.addData(FolderItem(folderId, folderName))
            isCompleteAddition.value = true
        }
    }

    fun updateFolderName(folder: FolderItem?, position: Int?) {
        val folderName = folderName.value?.trim()
        if (token == null || folder?.id == null || folderName == null) return // TODO 수정 실패 예외처리 필요
        viewModelScope.launch {
            isCompleteUpdate.value = folderRepository.updateFolderName(token, folder.id, folderName)
        }

        folder.name = folderName
        folderListAdapter.updateData(position ?: return, folder) // TODO 수정 완료 후 UI 업데이트
    }

    fun deleteFolder(folder: FolderItem?, position: Int?) {
        if (token == null || folder?.id == null) return // TODO 삭제 실패 예외처리 필요
        viewModelScope.launch {
            isCompleteDeletion.value = folderRepository.deleteFolder(token, folder.id)
        }
        folderListAdapter.deleteData(position ?: return, folder)
    }

    fun onFolderNameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        folderName.value = s.toString()
    }

    fun resetCompleteUpdate() {
        isCompleteUpdate.value = false
    }

    fun resetCompleteDeletion() {
        isCompleteDeletion.value = false
    }

    fun setFolderName(name: String) {
        folderName.value = name
    }

    fun setEditMode(isEditable: Boolean) {
        isEditMode.value = isEditable
    }

    fun getFolderListAdapter(): FolderListAdapter = folderListAdapter
    fun getFolderListSummaryAdapter(): FolderListAdapter = folderListSummaryAdapter
    fun getFolderName(): LiveData<String> = folderName
    fun getFolderItem(): FolderItem? = folderItem
    fun getIsCompleteAddition(): LiveData<Boolean> = isCompleteAddition
    fun getIsCompleteUpdate(): LiveData<Boolean> = isCompleteUpdate
    fun getIsCompleteDeletion(): LiveData<Boolean> = isCompleteDeletion
    fun getEditMode(): LiveData<Boolean> = isEditMode

    companion object {
        private const val TAG = "FolderViewModel"
    }
}