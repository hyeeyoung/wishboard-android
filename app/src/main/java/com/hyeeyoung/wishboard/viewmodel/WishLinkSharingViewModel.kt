package com.hyeeyoung.wishboard.viewmodel

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hyeeyoung.wishboard.model.wish.WishItemRegistrationInfo
import com.hyeeyoung.wishboard.repository.wish.WishRepository
import com.hyeeyoung.wishboard.util.prefs
import com.hyeeyoung.wishboard.util.safeLet
import dagger.hilt.android.lifecycle.HiltViewModel
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class WishLinkSharingViewModel @Inject constructor(
    private val wishRepository: WishRepository,
) : ViewModel() {
    private var itemName = MutableLiveData<String>()
    private var itemPrice = MutableLiveData<String>()
    private var itemImage = MutableLiveData<String>()
    private var itemMemo = MutableLiveData<String>()
    private var itemUrl = MutableLiveData<String>()
    private var isCompleteUpload = MutableLiveData<Boolean>()

    // TODO noti info 추가

    private val token = prefs?.getUserToken()

    suspend fun uploadWishList() {
        if (token == null) return
        // itemImage 없어도 업로드 되도록 수정
        safeLet(itemName.value, itemImage.value, itemUrl.value) { name, image, url ->
            val wishItemRegistrationInfo =
                WishItemRegistrationInfo(name, image, itemPrice.value ?: "0", url, itemMemo.value)
            val isComplete = wishRepository.uploadWishItem(token, wishItemRegistrationInfo)
            isCompleteUpload.postValue(isComplete)
            // TODO 예외처리
        }
    }

    /** 오픈그래프 메타태그 파싱을 통해 아이템 정보 가져오기 */
    suspend fun getWishItemInfo(url: String) {
        try {
            val doc = Jsoup.connect(url).get()
            val ogTags = doc.select("meta[property^=og:]")
            val priceTags = doc.select("meta[property^=product:]")

            if (ogTags.size <= 0) return
            for (idx in ogTags.indices) {
                val tag = ogTags[idx]
                val text = tag.attr("property")
                when (text) {
                    "og:title" -> itemName.postValue(tag.attr("content")) // 상품명 데이터 파싱
                    "og:image" -> itemImage.postValue(tag.attr("content")) // 상품 이미지 데이터 파싱
                }
            }

            // 가격 데이터 파싱
            if (priceTags.size > 0) {
                for (i in priceTags.indices) {
                    val priceTag = priceTags[i]
                    val text = priceTag.attr("property")
                    if (text.matches(Regex(".*[pP]rice.*"))) {
                        val price = priceTag.attr("content")
                        if (price.isDigitsOnly()) {
                            itemPrice.postValue(price)
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onItemNameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        itemName.value = s.toString().trim()
    }

    fun onItemPriceTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        itemPrice.value = s.toString().trim()
    }

    fun setItemUrl(url: String) {
        itemUrl.value = url
    }

    fun getItemName(): LiveData<String> = itemName
    fun getItemImage(): LiveData<String> = itemImage
    fun getItemPrice(): LiveData<String> = itemPrice
    fun isCompleteUpload(): LiveData<Boolean> = isCompleteUpload

    companion object {
        private const val TAG = "WishViewModel"
    }
}