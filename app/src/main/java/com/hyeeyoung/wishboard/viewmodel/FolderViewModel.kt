package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.common.ProcessStatus
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.model.folder.FolderListViewType
import com.hyeeyoung.wishboard.service.AWSS3Service
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

    private val folderList = MutableLiveData<List<FolderItem>?>(listOf())
    private val folderListAdapter =
        FolderListAdapter(FolderListViewType.SQUARE_VIEW_TYPE)
    private var folderName = MutableLiveData<String?>()
    private var folderItem: FolderItem? = null
    private var folderPosition: Int? = null

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
            withContext(Dispatchers.IO){
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
            updateFolderName(folderItem!!, folderPosition ?: return, folderName)
        } else {
            createNewFolder(folderName)
        }
    }

    private fun createNewFolder(folderName: String) {
        folderRegistrationStatus.value = ProcessStatus.IN_PROGRESS
        val folderInfo = FolderItem(name = folderName)

        viewModelScope.launch {
            folderItem = folderInfo
            val result = folderRepository.createNewFolder(token!!, folderInfo)
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

    private fun updateFolderName(folder: FolderItem, position: Int, folderName: String) {
        if (folder.id == null) return
        folderRegistrationStatus.value = ProcessStatus.IN_PROGRESS

        viewModelScope.launch {
            val result = folderRepository.updateFolderName(token!!, folder.id, folderName)
            if (result?.first == true) {
                folder.name = folderName
                folderListAdapter.updateData(position, folder)
            }
            isCompleteUpload.value = result?.first
            isExistFolderName.value = result?.second == 409
            folderRegistrationStatus.postValue(ProcessStatus.IDLE)
        }
    }

    fun deleteFolder(folder: FolderItem?, position: Int?) {
        if (token == null || folder?.id == null || position == null) return
        viewModelScope.launch {
            val result = folderRepository.deleteFolder(token, folder.id)
            if (result) {
                folderListAdapter.deleteData(position, folder)
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

    fun setFolderInfo(position: Int?, folder: FolderItem?) {
        this.folderPosition = position
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