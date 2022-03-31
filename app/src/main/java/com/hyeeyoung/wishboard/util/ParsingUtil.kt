package com.hyeeyoung.wishboard.util

import android.util.Log
import com.hyeeyoung.wishboard.model.wish.WishItem
import org.jsoup.Jsoup

class ParsingUtils {
    companion object {
        private const val TAG = "ParsingUtils"
        private var itemName: String? = null
        private var itemImage: String? = null
        private var itemPrice: Int? = null
    }

    /** 선택된 날짜에 해당하는 월간 달력을 반환 */
    fun onBindParsingType(url: String): WishItem {
        try {
            when {
                url.startsWith("https://m.musinsa.com/") || url.startsWith("https://musinsaapp.page.link/") -> {
                    parsingForMusinsa(url)
                }
                url.startsWith("https://m.wconcept.co.kr/") -> {
                    parsingForWconcept(url)
                }
                url.startsWith("https://m.smartstore.naver.com/") -> {
                    parsingForNaverStore(url)
                }
                url.startsWith("https://www.cosstores.com/") -> {
                    parsingForCos(url)
                }
                else -> {
                    parsingForGeneral(url)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val item = WishItem(name = itemName ?: "", image = itemImage, price = itemPrice)
        resetData()
        return item
    }

    /** 범용 */
    private fun parsingForGeneral(url: String) {
        val doc = Jsoup.connect(url).get()

        itemName = doc.select("meta[property=og:title]").first()?.attr("content")
        itemImage = doc.select("meta[property=og:image]").first()?.attr("abs:content")

        val priceTags = doc.select("meta[property^=product:]")
        if (priceTags.size > 0) {
            val text = priceTags[0].attr("property")
            if (text.matches(Regex(".*[pP]rice.*"))) {
                itemPrice = priceTags[0].attr("content").replace("[^0-9]".toRegex(), "").toInt()
            }
        }
    }

    /** W컨셉 */
    private fun parsingForWconcept(url: String) {
        val doc = Jsoup.connect(url).get()
        itemName = doc.select("meta[property=og:description]").first()?.attr("content")
        itemImage = doc.select("meta[property=og:image]").first()?.attr("abs:content")
        itemPrice = doc.select("meta[property=eg:salePrice]").first()?.attr("content")?.replace("[^0-9]".toRegex(), "")?.toInt()

        if (itemPrice == null) {
            itemPrice = doc.select("meta[property=eg:originalPrice]").first()?.attr("content")?.replace("[^0-9]".toRegex(), "")?.toInt()
        }
    }

    /** 무신사 */
    private fun parsingForMusinsa(url: String) {
        val doc = Jsoup.connect(url).get()
        itemName = doc.select("meta[property=og:title]").first()?.attr("content")
        itemImage = doc.select("meta[property=og:image]").first()?.attr("abs:content")
        itemPrice = doc.select(".product_article_price").first()?.text()?.replace("[^0-9]".toRegex(), "")?.toInt()
    }

    /** 네이버 스토어 */
    private fun parsingForNaverStore(url: String) {
        val doc = Jsoup.connect(url).get() ?: return
        Log.i(TAG, "parsingForGeneral: ${doc.select("meta[property=og:title]")}")
        itemName = doc.select("meta[property=og:title]").first()?.attr("content")
        itemImage = doc.select("meta[property=og:image]").first()?.attr("abs:content")

        val price = doc.select("._1LY7DqCnwR")
        if (price.size <= 0) return
        itemPrice = price[0].text().replace("[^0-9]".toRegex(), "").toInt()
    }

    /** 코스 */
    private fun parsingForCos(url: String) {
        val doc = Jsoup.connect(url).get()
        itemName = doc.select("title").first()?.text()
        itemImage = doc.select("a[href=#gallery-product-0] div img").first()?.attr("abs:src")
        itemPrice = doc.select("#priceValue").first()?.text()?.replace("[^0-9]".toRegex(), "")?.toInt()
    }

    private fun resetData() {
        itemName = ""
        itemImage = null
        itemPrice = null
    }
}