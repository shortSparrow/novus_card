package com.senya.novuswidget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.senya.novuswidget.domain.CardMapper
import com.senya.novuswidget.domain.model.ModifiedShopItem
import com.senya.novuswidget.ui.home.HomeAction
import com.senya.novuswidget.ui.home.HomeState
import com.senya.novuswidget.use_case.AddImageUseCase
import com.senya.novuswidget.use_case.GetCardsFromStorageUseCase
import java.util.*


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
                val modifiedCard = if (!action.isOpen) null else state.modifiedCard
                state =
                    state.copy(isAddNewCardModalOpen = action.isOpen, modifiedCard = modifiedCard)
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