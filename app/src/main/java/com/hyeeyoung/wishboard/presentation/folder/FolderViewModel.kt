package com.hyeeyoung.wishboard.presentation.folder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.domain.repositories.FolderRepository
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
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
    private val _folderFetchState = MutableStateFlow<UiState<List<FolderItem>>>(UiState.Init)
    val folderFetchState get() = _folderFetchState.asStateFlow()
    private val _folderDeleteState = MutableLiveData<UiState<Long>>(UiState.Init)
    val folderDeleteState: LiveData<UiState<Long>> get() = _folderDeleteState
    private val _folderAddState = MutableLiveData<UiState<FolderItem?>>(UiState.Init)
    val folderAddState: LiveData<UiState<FolderItem?>> get() = _folderAddState
    private val _folderUpdateState =
        MutableLiveData<UiState<Pair<FolderItem, FolderItem>>>(UiState.Init)
    val folderUpdateState: LiveData<UiState<Pair<FolderItem, FolderItem>>> get() = _folderUpdateState

    private val folderName = MutableLiveData<String?>()
    private var folderItem: FolderItem? = null

    private val isExistFolderName = MutableLiveData<Boolean?>()
    private var isEditMode: Boolean = false

    private val folderRegistrationStatus = MutableLiveData<ProcessStatus>()
    private val _folderCount = MutableStateFlow(0)
    val folderCount get() = _folderCount.asStateFlow()

    fun fetchFolderList() {
        viewModelScope.launch {
            folderRepository.fetchFolderList().let { folders ->
                _folderFetchState.value =
                    if (folders == null) UiState.Error(null)
                    else if (folders.isEmpty()) UiState.Empty
                    else UiState.Success(folders)
                _folderCount.value = folders?.size ?: 0
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
            val isSuccessful = result?.first?.first
            val folder = FolderItem(result?.second, folderName)
            _folderAddState.value =
                if (isSuccessful == true) {
                    _folderCount.value += 1
                    UiState.Success(folder)
                } else {
                    UiState.Error(null)
                }

            isExistFolderName.value = result?.first?.second == 409
            folderRegistrationStatus.value = ProcessStatus.IDLE
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
            val isSuccessful = result?.first
            _folderUpdateState.value =
                if (isSuccessful == true) UiState.Success(Pair(oldFolder, newFolder))
                else UiState.Error(null)
            isExistFolderName.value = result?.second == 409
            folderRegistrationStatus.value = ProcessStatus.IDLE
        }
    }

    fun deleteFolder(folder: FolderItem?) {
        if (folder?.id == null) return
        viewModelScope.launch {
            val isSuccessful = folderRepository.deleteFolder(folder.id)
            if (isSuccessful) {
                _folderDeleteState.value = UiState.Success(folder.id)
                _folderCount.value -= 1
            } else {
                _folderDeleteState.value = UiState.Error(null)
            }
        }
    }

    fun onFolderNameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        folderName.value = s.toString()
        isExistFolderName.value = false
    }

    /** 폴더 추가 후 또 다른 폴더 추가/수정을 위해 이전에 입력한 폴더명 등의 폴더 관련 데이터를 reset */
    fun resetFolderData() {
        folderName.value = null
        if (isEditMode) _folderUpdateState.value = UiState.Init
        else _folderAddState.value = UiState.Init
        isExistFolderName.value = null
    }

    fun resetCompleteDeletion() {
        _folderDeleteState.value = UiState.Init
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

    fun getFolderName(): LiveData<String?> = folderName
    fun getIsExistFolderName(): LiveData<Boolean?> = isExistFolderName
    fun getEditMode(): Boolean = isEditMode
    fun getRegistrationStatus(): LiveData<ProcessStatus> = folderRegistrationStatus
}