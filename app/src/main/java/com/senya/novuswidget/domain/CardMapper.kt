package com.senya.novuswidget.domain

import com.senya.novuswidget.domain.model.ModifiedShopItem
import com.senya.novuswidget.domain.model.ShopItem
import com.senya.novuswidget.domain.model.ShopItemSP

class CardMapper {
    fun shopItemToShopItemSP(shopItem: ShopItem): ShopItemSP = ShopItemSP(
        title = shopItem.title,
        path = shopItem.path,
        id = shopItem.id
    )

    fun shopItemSPToShopItem(shopItemSP: ShopItemSP): ShopItem = ShopItem(
        uri = null,
        path = shopItemSP.path,
        title = shopItemSP.title,
        id = shopItemSP.id
    )

    fun shopItemToModifiedShopItem(shopItem: ShopItem): ModifiedShopItem = ModifiedShopItem(
        title = shopItem.title,
        uri = shopItem.uri,
        path = shopItem.path,
        id = shopItem.id
    )

    companion object {
        val instance = CardMapper()
    }
}