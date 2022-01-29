package com.hyeeyoung.wishboard.repository.folder

import android.util.Log
import com.hyeeyoung.wishboard.model.folder.FolderItem
import com.hyeeyoung.wishboard.remote.RemoteService

class FolderRepositoryImpl : FolderRepository {
    private val api = RemoteService.api

    override suspend fun createNewFolder(token: String, folderItemInfo: FolderItem): Boolean {
        val response = api.createNewFolder(token, folderItemInfo)

        if (response.isSuccessful) {
            Log.d(TAG, "폴더 추가 성공")
        } else {
            Log.e(TAG, "폴더 추가 실패: ${response.code()}")
        }
        return response.isSuccessful
    }

    companion object {
        private const val TAG = "FolderRepositoryImpl"
    }
}