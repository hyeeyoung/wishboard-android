package com.hyeeyoung.wishboard.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        fetchFolderList()
    }

    fun fetchFolderList() {
        if (token == null) return
        viewModelScope.launch {
            folderListAdapter.setData(folderRepository.fetchFolderList(token) ?: return@launch)
        }
    }

    fun getFolderListAdapter(): FolderListAdapter = folderListAdapter

    companion object {
        private const val TAG = "FolderViewModel"
    }
}