package com.hyeeyoung.wishboard.data.repositories

import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.data.services.retrofit.FolderService
import com.hyeeyoung.wishboard.domain.repositories.FolderRepository
import timber.log.Timber
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(private val folderService: FolderService) :
    FolderRepository {
    override suspend fun fetchFolderList(token: String): List<FolderItem>? {
        try {
            val response = folderService.fetchFolderList(token) ?: return null
            if (response.isSuccessful) {
                Timber.d("폴더 가져오기 성공")
            } else {
                Timber.e("폴더 가져오기 실패: ${response.code()}")
            }
            return response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun fetchFolderListSummary(token: String): List<FolderItem>? {
        try {
            val response = folderService.fetchFolderListSummary(token) ?: return null
            if (response.isSuccessful) {
                Timber.d("폴더 요약본 가져오기 성공")
            } else {
                Timber.e("폴더 요약본 가져오기 실패: ${response.code()}")
            }
            return response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun fetchItemsInFolder(token: String, folderId: Long): List<WishItem>? {
        try {
            val response = folderService.fetchItemsInFolder(token, folderId) ?: return null
            if (response.isSuccessful) {
                Timber.d("폴더 내 아이템 가져오기 성공")
            } else {
                Timber.e("폴더 내 아이템 가져오기 실패: ${response.code()}")
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
            val response = folderService.createNewFolder(token, folderItemInfo)
            if (response.isSuccessful) {
                Timber.d("폴더 추가 성공")
            } else {
                Timber.e("폴더 추가 실패: ${response.code()}")
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
            val response = folderService.updateFolderName(token, folderId, folderName)

            if (response.isSuccessful) {
                Timber.d("폴더명 수정 성공")
            } else {
                Timber.e("폴더명 수정 실패: ${response.code()}")
            }
            return Pair(response.isSuccessful, response.code())
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun deleteFolder(token: String, folderId: Long): Boolean {
        try {
            val response = folderService.deleteFolder(token, folderId)
            if (response.isSuccessful) {
                Timber.d("폴더 삭제 성공")
            } else {
                Timber.e("폴더 삭제 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}