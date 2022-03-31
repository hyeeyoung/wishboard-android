package com.hyeeyoung.wishboard.repository.folder

import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.model.wish.WishItem

interface FolderRepository {
    suspend fun fetchFolderList(token: String): List<FolderItem>?
    suspend fun fetchFolderListSummary(token: String): List<FolderItem>?
    suspend fun fetchItemsInFolder(token: String, folderId: Long): List<WishItem>?
    /** [return type] first.first : isSuccessful, first.second : responseCode, second : folderId */
    suspend fun createNewFolder(
        token: String,
        folderItemInfo: FolderItem
    ): Pair<Pair<Boolean, Int>, Long?>?

    /** [return type] first : isSuccessful, second : responseCode */
    suspend fun updateFolderName(
        token: String,
        folderId: Long,
        folderName: String
    ): Pair<Boolean, Int>?
    suspend fun deleteFolder(token: String, folderId: Long): Boolean
}