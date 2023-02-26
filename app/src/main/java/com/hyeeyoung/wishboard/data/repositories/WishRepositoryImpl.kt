package com.hyeeyoung.wishboard.data.repositories

import com.hyeeyoung.wishboard.data.model.wish.ItemDetail
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
    override suspend fun fetchWishList(): List<WishItem>? = runCatching {
        wishItemService.fetchWishList()
    }.fold({
        Timber.d("아이템 가져오기 성공(${it.code()})")
        it.body()
    }, {
        Timber.d("아이템 가져오기 실패: ${it.message}")
        null
    })

    override suspend fun fetchLatestWishItem(): WishItem? =
        runCatching {
            wishItemService.fetchLatestWishItem()
        }.fold({
            Timber.d("가장 최근 등록된 아이템 가져오기 성공(${it?.code()})")
            it?.body()?.get(0)
        }, {
            Timber.e("가장 최근 등록된 아이템 가져오기 실패: ${it.message}")
            null
        })

    override suspend fun fetchWishItemDetail(itemId: Long): List<ItemDetail>? =
        runCatching {
            wishItemService.fetchWishItemDetail(itemId)
        }.fold({
            Timber.d("아이템 상세정보 가져오기 성공(${it.code()})")
            it.body()
        }, {
            Timber.e("아이템 상세정보 가져오기 실패: ${it.message}")
            null
        })

    override suspend fun uploadWishItem(
        folderId: RequestBody?,
        itemName: RequestBody,
        itemPrice: RequestBody?,
        itemUrl: RequestBody?,
        itemNotificationType: RequestBody?,
        itemNotificationDate: RequestBody?,
        image: MultipartBody.Part?,
        itemMemo: RequestBody?,
    ): Boolean = runCatching {
        wishItemService.uploadWishItem(
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
        itemId: Long,
        folderId: RequestBody?,
        itemName: RequestBody,
        itemPrice: RequestBody?,
        itemMemo: RequestBody?,
        itemUrl: RequestBody?,
        itemNotificationType: RequestBody?,
        itemNotificationDate: RequestBody?,
        itemImage: MultipartBody.Part?
    ): Pair<Boolean, Int>? = runCatching {
        wishItemService.updateToWishItem(
            itemId, folderId,
            itemName,
            itemPrice,
            itemMemo,
            itemUrl,
            itemNotificationType,
            itemNotificationDate,
            itemImage
        )
    }.fold({
        Timber.d("아이템 수정 성공(${it.code()})")
        Pair(it.isSuccessful, it.code())
    }, {
        Timber.e("아이템 수정 실패: ${it.message}")
        null
    })

    override suspend fun updateFolderOfWishItem(itemId: Long, folderId: Long): Boolean =
        runCatching {
            wishItemService.updateFolderOfItem(itemId, folderId)
        }.fold({
            Timber.d("아이템 폴더 수정 성공(${it.code()})")
            it.isSuccessful
        }, {
            Timber.e("아이템 폴더 수정 실패: ${it.message}")
            false
        })

    override suspend fun deleteWishItem(itemId: Long): Boolean = runCatching {
        wishItemService.deleteWishItem(itemId)
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
            Timber.d("아이템 파싱 성공(${it.code()})")
            Pair(it.body()?.data, it.code())
        }, {
            Timber.d("아이템 파싱 실패: ${it.message}")
            null
        })
    }
}