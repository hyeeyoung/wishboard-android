package com.hyeeyoung.wishboard.repository.folder

import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.model.wish.WishItem

interface FolderRepository {
    suspend fun fetchFolderList(token: String): List<FolderItem>?
    suspend fun fetchItemsInFolder(token: String, folderId: Long): List<WishItem>?
    suspend fun createNewFolder(token: String, folderItemInfo: FolderItem): Boolean
}