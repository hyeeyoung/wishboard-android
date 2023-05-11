package com.hyeeyoung.wishboard.presentation.folder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.repositories.FolderRepository
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.presentation.folder.types.FolderListViewType
import com.hyeeyoung.wishboard.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val folderRepository: FolderRepository,
) : ViewModel() {
    private val _folderFetchState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val folderFetchState get() = _folderFetchState.asStateFlow()
    private val folderList = MutableLiveData<List<FolderItem>?>(listOf())
    private val folderListAdapter =
        FolderListAdapter(FolderListViewType.VERTICAL_VIEW_TYPE)
    private var folderName = MutableLiveData<String?>()
    private var folderItem: FolderItem? = null

    private var isCompleteUpload = MutableLiveData<Boolean?>()
    private var isCompleteDeletion = MutableLiveData<Boolean>()
    private var isExistFolderName = MutableLiveData<Boolean?>()
    private var isEditMode: Boolean = false

    private var folderRegistrationStatus = MutableLiveData<ProcessStatus>()

    fun fetchFolderList() {
        viewModelScope.launch {
            folderRepository.fetchFolderList().let { folders ->
                _folderFetchState.value =
                    if (folders == null) UiState.Error(null) else UiState.Success(true)
                folderList.value = folders
                folderListAdapter.setData(folders)
            }
        }
    }

    fun uploadFolder() {
        if (folderRegistrationStatus.value == ProcessStatus.IN_PROGRESS) return
        val folderName = folderName.value?.trim() ?: return

        if (isEditMode) {
            updateFolderName(folderItem!!, folderName)
        } else {
            createNewFolder(folderName)
        }
    }

    private fun createNewFolder(folderName: String) {
        folderRegistrationStatus.value = ProcessStatus.IN_PROGRESS
        viewModelScope.launch {
            val result = folderRepository.createNewFolder(FolderItem(name = folderName))
            isCompleteUpload.value = result?.first?.first
            isExistFolderName.value = result?.first?.second == 409
            result?.second?.let { folderId ->
                val folder = FolderItem(folderId, folderName)
                folderListAdapter.addData(folder)
                folderList.postValue(folderListAdapter.getData())
            }
            folderRegistrationStatus.postValue(ProcessStatus.IDLE)
        }
    }

    private fun updateFolderName(oldFolder: FolderItem, folderName: String) {
        if (oldFolder.id == null) return
        folderRegistrationStatus.value = ProcessStatus.IN_PROGRESS

        val newFolder = oldFolder.apply {
            this.name = folderName
        }

        viewModelScope.launch {
            val result = folderRepository.updateFolderName(oldFolder.id, folderName)
            if (result?.first == true) {
                folderListAdapter.updateData(oldFolder, newFolder)
            }
            isCompleteUpload.value = result?.first
            isExistFolderName.value = result?.second == 409
            folderRegistrationStatus.postValue(ProcessStatus.IDLE)
        }
    }

    fun deleteFolder(folder: FolderItem?) {
        if (folder?.id == null) return
        viewModelScope.launch {
            val result = folderRepository.deleteFolder(folder.id)
            if (result) {
                folderListAdapter.deleteData(folder)
                folderList.value = folderListAdapter.getData()
            }
            isCompleteDeletion.postValue(result)
        }
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

    fun setFolderInfo(folder: FolderItem?) {
        folderItem = folder
        folderName.value = folder?.name
    }

    fun setEditMode(isEditable: Boolean) {
        isEditMode = isEditable
    }

    fun getFolderList(): LiveData<List<FolderItem>?> = folderList
    fun getFolderListAdapter(): FolderListAdapter = folderListAdapter
    fun getFolderName(): LiveData<String?> = folderName
    fun getIsCompleteUpload(): LiveData<Boolean?> = isCompleteUpload
    fun getIsCompleteDeletion(): LiveData<Boolean> = isCompleteDeletion
    fun getIsExistFolderName(): LiveData<Boolean?> = isExistFolderName
    fun getEditMode(): Boolean = isEditMode
    fun getRegistrationStatus(): LiveData<ProcessStatus> = folderRegistrationStatus
}