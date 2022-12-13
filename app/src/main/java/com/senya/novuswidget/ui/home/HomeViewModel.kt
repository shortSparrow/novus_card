package com.senya.novuswidget.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senya.novuswidget.domain.CardMapper
import com.senya.novuswidget.domain.model.ModifiedShopItem
import com.senya.novuswidget.domain.model.ShopItem
import com.senya.novuswidget.use_case.ModifyImageUseCase
import com.senya.novuswidget.use_case.GetCardsFromStorageUseCase
import com.senya.novuswidget.use_case.UpdateWidgetUseCase
import kotlinx.coroutines.launch
import java.util.*


class HomeViewModel : ViewModel() {
    var state by mutableStateOf(HomeState())
    private val modifyImageUseCase = ModifyImageUseCase()
    private val updateWidgetUseCase = UpdateWidgetUseCase()

    init {
        state = state.copy(cardList = state.cardList.plus(GetCardsFromStorageUseCase().invoke()))
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.AddImage -> {
                val newCardList = modifyImageUseCase.addNewImage(
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
            is HomeAction.SetIsOpenChangeOrderModal -> {
                state = state.copy(isOpenCardOrderModal = action.isOpen)
            }
            is HomeAction.SetNewCardOrder -> {
                state = state.copy(cardList = action.cardList)
                viewModelScope.launch {
                    modifyImageUseCase.updateSharedPreferencesData(action.cardList)
                    updateWidgetUseCase.updateWidget()
                }
            }
            HomeAction.DeleteImage -> {
                state =
                    state.copy(cardList = state.cardList.filter { it.id != state.modifiedCard?.id }, isAddNewCardModalOpen = false)
                val removeItem= CardMapper.instance.modifiedShopItemToShopItem(
                    state.modifiedCard!! // I am sure, that path exist, because we delete already existing file
                )
                viewModelScope.launch {
                    modifyImageUseCase.removeImage(
                        deletedItem = removeItem,
                        newCardList = state.cardList
                    )
                    updateWidgetUseCase.updateWidget()
                }
            }
        }
    }
}