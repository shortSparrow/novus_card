package com.senya.novuswidget.use_case

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.senya.novuswidget.DiscountCardApp
import com.senya.novuswidget.MainActivity
import com.senya.novuswidget.domain.CardMapper
import com.senya.novuswidget.domain.model.ShopItem
import com.senya.novuswidget.domain.model.ShopItemSP
import com.senya.novuswidget.util.DISCOUNT_CARDS_INFO
import com.senya.novuswidget.util.DISCOUNT_CARDS_LIST
import java.lang.reflect.Type


class GetCardsFromStorageUseCase() {
    operator fun invoke(): List<ShopItem> {
        val gson = Gson()

        DiscountCardApp.applicationContext().getSharedPreferences(DISCOUNT_CARDS_INFO, Context.MODE_PRIVATE).apply {
            val type: Type = object : TypeToken<List<ShopItemSP?>?>() {}.type
            val savedCards = getString(DISCOUNT_CARDS_LIST, "")

            val cardList: List<ShopItemSP> = if (savedCards?.isNotEmpty() == true) {
                gson.fromJson(savedCards, type)
            } else {
                emptyList()
            }
            return cardList.map {
                CardMapper.instance.shopItemSPToShopItem(it)
            }
        }
    }
}