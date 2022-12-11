package com.senya.novuswidget

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.senya.novuswidget.use_case.AddImageUseCase
import com.senya.novuswidget.use_case.GetCardsFromStorageUseCase
import java.util.*

data class HomeState(
    val cardList: List<ShopItem> = emptyList(),
    val isAddNewCardModalOpen: Boolean = false,
    val modifiedCard: ModifiedShopItem? = null,
)

data class ShopItem(
    val uri: Uri?,
    val path: String,
    val title: String = "",
    val id: String
)

data class ShopItemSP(
    val path: String,
    val title: String = "",
    val id: String
)


data class ModifiedShopItem(
    val uri: Uri? = null, // for case get image from gallery
    val path: String? = null, // for case load image from path
    val title: String = "",
    val id: String,
)

sealed interface HomeAction {
    data class ToggleOpenAddNewCardModal(val isOpen: Boolean) : HomeAction
    data class AddTempModifiedImage(val uri: Uri?) : HomeAction
    data class OnChangeCardTitle(val value: String) : HomeAction
    data class SetModifiedCard(val card: ShopItem) : HomeAction
    object AddImage : HomeAction
}

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

val gson = Gson()

class HomeViewModel : ViewModel() {
    var state by mutableStateOf(HomeState())

    init {
        state = state.copy(cardList = state.cardList.plus(GetCardsFromStorageUseCase().invoke()))
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.AddImage -> {
                val newCardList = AddImageUseCase().invoke(
                    modifiedCard = state.modifiedCard,
                    cardList = state.cardList
                )
                state = state.copy(
                    cardList = newCardList,
                    modifiedCard = null,
                    isAddNewCardModalOpen = false
                )
            }
            is HomeAction.ToggleOpenAddNewCardModal -> {
                state = state.copy(isAddNewCardModalOpen = action.isOpen)
            }
            is HomeAction.OnChangeCardTitle -> {
                val modifiedImage =
                    state.modifiedCard ?: ModifiedShopItem(id = UUID.randomUUID().toString())
                state = state.copy(modifiedCard = modifiedImage.copy(title = action.value))
            }
            is HomeAction.AddTempModifiedImage -> {
                val modifiedImage =
                    state.modifiedCard ?: ModifiedShopItem(id = UUID.randomUUID().toString())
                state = state.copy(modifiedCard = modifiedImage.copy(uri = action.uri))
            }
            is HomeAction.SetModifiedCard -> {
                val modifiedCard = CardMapper.instance.shopItemToModifiedShopItem(action.card)
                state = state.copy(modifiedCard = modifiedCard, isAddNewCardModalOpen = true)
            }
        }
    }
}