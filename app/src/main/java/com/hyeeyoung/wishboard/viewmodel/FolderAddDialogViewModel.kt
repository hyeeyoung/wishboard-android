package com.hyeeyoung.wishboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.repository.folder.FolderRepository
import com.hyeeyoung.wishboard.util.prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderAddDialogViewModel @Inject constructor(
    private val folderRepository: FolderRepository,
) : ViewModel() {
    private val token = prefs?.getUserToken()

    private var folderName = MutableLiveData<String>()
    private var folderItem: FolderItem? = null
    private var isCompleteAddition = MutableLiveData<Boolean>()

    fun createNewFolder() {
        val folderName = folderName.value?.trim()
        if (token == null || folderName == null) return
        val folderInfo = FolderItem(name = folderName)

        viewModelScope.launch {
            folderItem = folderInfo
            isCompleteAddition.value = folderRepository.createNewFolder(token, folderInfo)
        }
    }

    fun onFolderNameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        folderName.value = s.toString()
    }

    fun getFolderName(): LiveData<String> = folderName
    fun getFolderItem(): FolderItem? = folderItem
    fun getIsCompleteAddition(): LiveData<Boolean> = isCompleteAddition

    companion object {
        private const val TAG = "FolderAddDialogViewModel"
    }
}