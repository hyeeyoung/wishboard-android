package com.hyeeyoung.wishboard.domain.repositories

import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem

interface FolderRepository {
    suspend fun fetchFolderList(): List<FolderItem>?
    suspend fun fetchFolderListSummary(): List<FolderItem>?
    suspend fun fetchItemsInFolder(folderId: Long): List<WishItem>?

    /** [return type] first.first : isSuccessful, first.second : responseCode, second : folderId */
    suspend fun createNewFolder(folderItemInfo: FolderItem): Pair<Pair<Boolean, Int>, Long?>?

    /** [return type] first : isSuccessful, second : responseCode */
    suspend fun updateFolderName(folderId: Long, folderName: String): Pair<Boolean, Int>?
    suspend fun deleteFolder(folderId: Long): Boolean
}