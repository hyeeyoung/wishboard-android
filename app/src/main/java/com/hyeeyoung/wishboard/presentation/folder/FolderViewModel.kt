package com.hyeeyoung.wishboard.presentation.folder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.WishBoardApp
import com.hyeeyoung.wishboard.presentation.common.types.ProcessStatus
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.presentation.folder.types.FolderListViewType
import com.hyeeyoung.wishboard.domain.repositories.FolderRepository
import com.hyeeyoung.wishboard.data.services.AWSS3Service
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val folderRepository: FolderRepository,
) : ViewModel() {
    private val token = WishBoardApp.prefs.getUserToken()

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

    init {
        fetchFolderList()
    }

    fun fetchFolderList() {
        if (token == null) return
        viewModelScope.launch {
            var items: List<FolderItem>?
            withContext(Dispatchers.IO) {
                items = folderRepository.fetchFolderList(token)
                items?.forEach { item ->
                    item.thumbnail?.let {
                        item.thumbnailUrl = AWSS3Service().getImageUrl(it)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                folderList.postValue(items)
                folderListAdapter.setData(items)
            }
        }
    }

    fun uploadFolder() {
        if (folderRegistrationStatus.value == ProcessStatus.IN_PROGRESS) return
        if (token == null) return
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
            val result = folderRepository.createNewFolder(token!!, FolderItem(name = folderName))
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
            val result = folderRepository.updateFolderName(token!!, oldFolder.id, folderName)
            if (result?.first == true) {
                folderListAdapter.updateData(oldFolder, newFolder)
            }
            isCompleteUpload.value = result?.first
            isExistFolderName.value = result?.second == 409
            folderRegistrationStatus.postValue(ProcessStatus.IDLE)
        }
    }

    fun deleteFolder(folder: FolderItem?) {
        if (token == null || folder?.id == null) return
        viewModelScope.launch {
            val result = folderRepository.deleteFolder(token, folder.id)
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

    companion object {
        private const val TAG = "FolderViewModel"
    }
}