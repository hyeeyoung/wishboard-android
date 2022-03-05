package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.common.ProcessStatus
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.model.folder.FolderListViewType
import com.hyeeyoung.wishboard.remote.AWSS3Service
import com.hyeeyoung.wishboard.repository.folder.FolderRepository
import com.hyeeyoung.wishboard.util.prefs
import com.hyeeyoung.wishboard.view.folder.adapters.FolderListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val folderRepository: FolderRepository,
) : ViewModel() {
    private val token = prefs?.getUserToken()

    private val folderListAdapter =
        FolderListAdapter(FolderListViewType.SQUARE_VIEW_TYPE)
    private var folderName = MutableLiveData<String?>()
    private var folderItem: FolderItem? = null

    private var isCompleteUpload = MutableLiveData<Boolean>()
    private var isCompleteDeletion = MutableLiveData<Boolean>()
    private var isExistFolderName = MutableLiveData<Boolean>()
    private var isEditMode = MutableLiveData<Boolean>()

    private var folderRegistrationStatus = MutableLiveData<ProcessStatus>()

    fun fetchFolderList() {
        if (token == null) return
        viewModelScope.launch {
            var items: List<FolderItem>?
            withContext(Dispatchers.IO){
                items = folderRepository.fetchFolderList(token)
                items?.forEach { item ->
                    item.thumbnail?.let {
                        item.thumbnailUrl = AWSS3Service().getImageUrl(it)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                folderListAdapter.setData(items?: return@withContext)
            }
        }
    }

    fun createNewFolder() {
        folderRegistrationStatus.value = ProcessStatus.IN_PROGRESS
        val folderName = folderName.value?.trim()
        if (token == null || folderName == null) return
        val folderInfo = FolderItem(name = folderName)

        viewModelScope.launch {
            folderItem = folderInfo
            val result = folderRepository.createNewFolder(token, folderInfo)
            isCompleteUpload.value = result.first.first
            isExistFolderName.value = result.first.second == 409
            result.second?.let { folderId ->
                folderListAdapter.addData(FolderItem(folderId, folderName))
            }
            folderRegistrationStatus.postValue(ProcessStatus.IDLE)
        }
    }

    fun updateFolderName(folder: FolderItem?, position: Int?) {
        folderRegistrationStatus.value = ProcessStatus.IN_PROGRESS
        val folderName = folderName.value?.trim()
        if (token == null || folder?.id == null || folderName == null) return // TODO 수정 실패 예외처리 필요
        viewModelScope.launch {
            val result = folderRepository.updateFolderName(token, folder.id, folderName)
            isCompleteUpload.value = result.first
            isExistFolderName.value = result.second == 409
            folderRegistrationStatus.postValue(ProcessStatus.IDLE)
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
        isExistFolderName.value = false
    }

    /** 폴더 추가 후 또 다른 폴더 추가/수정을 위해 이전에 입력한 폴더명 등의 폴더 관련 데이터를 reset */
    fun resetFolderData() {
        folderName.value = null
        isCompleteUpload.value = null
        isExistFolderName.value = null
    }

    fun resetCompleteDeletion() {
        isCompleteDeletion.value = false
    }

    fun resetFolderName() {
        folderName.value = null
    }

    fun setFolderName(name: String) {
        folderName.value = name
    }

    fun setEditMode(isEditable: Boolean) {
        isEditMode.value = isEditable
    }

    fun getFolderListAdapter(): FolderListAdapter = folderListAdapter
    fun getFolderName(): LiveData<String?> = folderName
    fun getIsCompleteUpload(): LiveData<Boolean> = isCompleteUpload
    fun getIsCompleteDeletion(): LiveData<Boolean> = isCompleteDeletion
    fun getIsExistFolderName(): LiveData<Boolean> = isExistFolderName
    fun getEditMode(): LiveData<Boolean> = isEditMode
    fun getRegistrationStatus(): LiveData<ProcessStatus> = folderRegistrationStatus

    companion object {
        private const val TAG = "FolderViewModel"
    }
}