package com.senya.novuswidget.ui.home

import com.senya.novuswidget.domain.model.ModifiedShopItem
import com.senya.novuswidget.domain.model.ShopItem

data class HomeState(
    val cardList: List<ShopItem> = emptyList(),
    val isAddNewCardModalOpen: Boolean = false,
    val modifiedCard: ModifiedShopItem? = null,
)
