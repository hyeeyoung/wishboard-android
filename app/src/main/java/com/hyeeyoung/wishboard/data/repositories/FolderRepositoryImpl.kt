package com.hyeeyoung.wishboard.data.repositories

import android.util.Log
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.domain.repositories.FolderRepository
import com.hyeeyoung.wishboard.data.services.RemoteService

class FolderRepositoryImpl : FolderRepository {
    private val api = RemoteService.api

    override suspend fun fetchFolderList(token: String): List<FolderItem>? {
        try {
            val response = api.fetchFolderList(token) ?: return null
            if (response.isSuccessful) {
                Log.d(TAG, "폴더 가져오기 성공")
            } else {
                Log.e(TAG, "폴더 가져오기 실패: ${response.code()}")
            }
            return response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun fetchFolderListSummary(token: String): List<FolderItem>? {
        try {
            val response = api.fetchFolderListSummary(token) ?: return null
            if (response.isSuccessful) {
                Log.d(TAG, "폴더 요약본 가져오기 성공")
            } else {
                Log.e(TAG, "폴더 요약본 가져오기 실패: ${response.code()}")
            }
            return response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun fetchItemsInFolder(token: String, folderId: Long): List<WishItem>? {
        try {
            val response = api.fetchItemsInFolder(token, folderId) ?: return null
            if (response.isSuccessful) {
                Log.d(TAG, "폴더 내 아이템 가져오기 성공")
            } else {
                Log.e(TAG, "폴더 내 아이템 가져오기 실패: ${response.code()}")
            }
            return response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun createNewFolder(
        token: String,
        folderItemInfo: FolderItem
    ): Pair<Pair<Boolean, Int>, Long?>? {
        try {
            val response = api.createNewFolder(token, folderItemInfo)
            if (response.isSuccessful) {
                Log.d(TAG, "폴더 추가 성공")
            } else {
                Log.e(TAG, "폴더 추가 실패: ${response.code()}")
            }
            return Pair(Pair(response.isSuccessful, response.code()), response.body()?.data?.id)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun updateFolderName(
        token: String,
        folderId: Long,
        folderName: String
    ): Pair<Boolean, Int>? {
        try {
            val response = api.updateFolderName(token, folderId, folderName)

            if (response.isSuccessful) {
                Log.d(TAG, "폴더명 수정 성공")
            } else {
                Log.e(TAG, "폴더명 수정 실패: ${response.code()}")
            }
            return Pair(response.isSuccessful, response.code())
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun deleteFolder(token: String, folderId: Long): Boolean {
        try {
            val response = api.deleteFolder(token, folderId)
            if (response.isSuccessful) {
                Log.d(TAG, "폴더 삭제 성공")
            } else {
                Log.e(TAG, "폴더 삭제 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    companion object {
        private const val TAG = "FolderRepositoryImpl"
    }
}