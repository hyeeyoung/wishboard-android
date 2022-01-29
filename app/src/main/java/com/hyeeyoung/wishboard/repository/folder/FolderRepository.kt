package com.hyeeyoung.wishboard.repository.folder

import com.hyeeyoung.wishboard.model.folder.FolderItem

interface FolderRepository {
    suspend fun createNewFolder(token: String, folderItemInfo: FolderItem): Boolean
}