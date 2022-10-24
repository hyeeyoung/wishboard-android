package com.hyeeyoung.wishboard.data.repositories

import android.util.Log
import com.hyeeyoung.wishboard.data.model.wish.ItemInfo
import com.hyeeyoung.wishboard.data.model.wish.WishItem
import com.hyeeyoung.wishboard.data.services.retrofit.WishItemService
import com.hyeeyoung.wishboard.domain.repositories.WishRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import javax.inject.Inject

class WishRepositoryImpl @Inject constructor(private val wishItemService: WishItemService) :
    WishRepository {
    override suspend fun fetchWishList(token: String): List<WishItem>? {
        try {
            val response = wishItemService.fetchWishList(token)
            if (response.isSuccessful) {
                Log.d(TAG, "아이템 가져오기 성공")
            } else {
                Log.e(TAG, "아이템 가져오기 실패: ${response.code()}")
            }
            return response.body()
        } catch (e: Exception) {
            Timber.e(e.message)
            return null
        }
    }

    override suspend fun fetchLatestWishItem(token: String): WishItem? {
        try {
            val response = wishItemService.fetchLatestWishItem(token) ?: return null
            if (response.isSuccessful) {
                Log.d(TAG, "가장 최근 등록된 아이템 가져오기 성공")
            } else {
                Log.e(TAG, "가장 최근 등록된 아이템 가져오기 실패: ${response.code()}")
            }
            return response.body()?.get(0)
        } catch (e: Exception) {
            Timber.e(e.message)
            return null
        }
    }

    override suspend fun uploadWishItem(
        token: String,
        folderId: RequestBody,
        itemName: RequestBody,
        itemPrice: RequestBody,
        itemMemo: RequestBody,
        itemUrl: RequestBody,
        itemNotificationType: RequestBody,
        itemNotificationDate: RequestBody,
        image: MultipartBody.Part
    ): Boolean {
        try {
            val response =
                wishItemService.uploadWishItem(token, folderId, itemName, itemPrice, itemMemo, itemUrl, itemNotificationType, itemNotificationDate, image)
            if (response.isSuccessful) {
                Log.d(TAG, "아이템 등록 성공")
            } else {
                Log.e(TAG, "아이템 등록 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            Timber.e(e.message)
            return false
        }
    }

    override suspend fun updateWishItem(token: String, itemId: Long, wishItem: WishItem): Pair<Boolean, Int>? {
        return try {
            val response = wishItemService.updateToWishItem(token, itemId, wishItem)
            if (response.isSuccessful) {
                Log.d(TAG, "아이템 수정 성공")
            } else {
                Log.e(TAG, "아이템 수정 실패: ${response.code()}")
            }
            Pair(response.isSuccessful, response.code())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun updateFolderOfWishItem(
        token: String,
        itemId: Long,
        folderId: Long
    ): Boolean {
        try {
            val response = wishItemService.updateFolderOfItem(token, itemId, folderId)
            if (response.isSuccessful) {
                Log.d(TAG, "아이템 폴더 수정 성공")
            } else {
                Log.e(TAG, "아이템 폴더 수정 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun deleteWishItem(token: String, itemId: Long): Boolean {
        try {
            val response = wishItemService.deleteWishItem(token, itemId)
            if (response.isSuccessful) {
                Log.d(TAG, "아이템 삭제 성공")
            } else {
                Log.e(TAG, "아이템 삭제 실패: ${response.code()}")
            }
            return response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun getItemParsingInfo(site: String): ItemInfo? {
        return runCatching {
            wishItemService.getItemParsingInfo(site)
        }.fold({
            Log.d(TAG, "아이템 파싱 성공")
            it.body()?.data
        }, {
            Log.d(TAG, "아이템 파싱 실패: ${it.message}")
            null
        })
    }

    companion object {
        private const val TAG = "WishRepositoryImpl"
    }
}