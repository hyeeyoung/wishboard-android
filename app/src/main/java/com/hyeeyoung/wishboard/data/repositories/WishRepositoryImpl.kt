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
    override suspend fun fetchWishList(token: String): List<WishItem>? = runCatching {
        wishItemService.fetchWishList(token)
    }.fold({
        Timber.d("아이템 가져오기 성공(${it.code()})")
        it.body()
    }, {
        Timber.d("아이템 가져오기 실패: ${it.message}")
        null
    })

    override suspend fun fetchLatestWishItem(token: String): WishItem? =
        runCatching {
            wishItemService.fetchLatestWishItem(token)
        }.fold({
            Timber.d("가장 최근 등록된 아이템 가져오기 성공(${it?.code()})")
            it?.body()?.get(0)
        }, {
            Timber.e("가장 최근 등록된 아이템 가져오기 실패: ${it.message}")
            null
        })

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
    ): Boolean = runCatching {
        wishItemService.uploadWishItem(
            token,
            folderId,
            itemName,
            itemPrice,
            itemMemo,
            itemUrl,
            itemNotificationType,
            itemNotificationDate,
            image
        )
    }.fold({
        Timber.d("아이템 등록 성공(${it.code()})")
        it.isSuccessful
    }, {
        Timber.e("아이템 등록 실패(${it.message})")
        false
    })

    override suspend fun updateWishItem(
        token: String,
        itemId: Long,
        wishItem: WishItem
    ): Pair<Boolean, Int>? = runCatching {
        wishItemService.updateToWishItem(token, itemId, wishItem)
    }.fold({
        Timber.d("아이템 수정 성공(${it.code()})")
        Pair(it.isSuccessful, it.code())
    }, {
        Timber.e("아이템 수정 실패: ${it.message}")
        null
    })

    override suspend fun updateFolderOfWishItem(
        token: String,
        itemId: Long,
        folderId: Long
    ): Boolean = runCatching {
        wishItemService.updateFolderOfItem(token, itemId, folderId)
    }.fold({
        Timber.d("아이템 폴더 수정 성공(${it.code()})")
        it.isSuccessful
    }, {
        Timber.e("아이템 폴더 수정 실패: ${it.message}")
        false
    })

    override suspend fun deleteWishItem(token: String, itemId: Long): Boolean = runCatching {
        wishItemService.deleteWishItem(token, itemId)
    }.fold({
        Timber.d("아이템 삭제 성공(${it.code()})")
        it.isSuccessful
    }, {
        Timber.e("아이템 삭제 실패: ${it.message}")
        false
    })

    // TODO need refactoring
    override suspend fun getItemParsingInfo(site: String): Pair<ItemInfo?, Int>? {
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