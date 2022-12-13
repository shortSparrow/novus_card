package com.senya.novuswidget.ui.home

import android.net.Uri
import com.senya.novuswidget.domain.model.ShopItem

sealed interface HomeAction {
    data class ToggleOpenAddNewCardModal(val isOpen: Boolean) : HomeAction
    data class AddTempModifiedImage(val uri: Uri?) : HomeAction
    data class OnChangeCardTitle(val value: String) : HomeAction
    data class SetModifiedCard(val card: ShopItem) : HomeAction
    object AddImage : HomeAction
    data class SetIsOpenChangeOrderModal (val isOpen: Boolean): HomeAction
    data class SetNewCardOrder(val cardList: List<ShopItem>): HomeAction
}
