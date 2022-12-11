package com.senya.novuswidget

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.senya.novuswidget.use_case.AddImageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Type
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
        MainActivity.activityContext().getSharedPreferences("cards", Context.MODE_PRIVATE).apply {
            val type: Type = object : TypeToken<List<ShopItemSP?>?>() {}.type
            val savedCards = getString("items", "")

            val cardList: List<ShopItemSP> = if (savedCards?.isNotEmpty() == true) {
                gson.fromJson(savedCards, type)
            } else {
                emptyList()
            }
            val parsedImage = cardList.map {
                CardMapper.instance.shopItemSPToShopItem(it)
            }

            state = state.copy(cardList = state.cardList.plus(parsedImage))
        }
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

//                val context = MainActivity.activityContext()
//                state.modifiedCard?.uri?.let { uri ->
//                    // TODO if this old image avoid stream
//                    val stream = context.contentResolver.openInputStream(uri)
//                    val image = BitmapFactory.decodeStream(stream)
//                    val cw = ContextWrapper(context.applicationContext)
//                    val directory = cw.getDir("discount_cards", Context.MODE_PRIVATE)
//                    if (!directory.exists()) {
//                        directory.mkdir()
//                    }
//                    // todo fix format, not olways png
//                    val file = File(directory, uri.path!!.split("/").last())
//
//                    val editableCardOldVersion = state.cardList.find { it.id == state.modifiedCard?.id }
//                    val isEditableCard = editableCardOldVersion != null
//                    val isImageTheSame =editableCardOldVersion?.path == file.path
//
//                    viewModelScope.launch {
//                        withContext(Dispatchers.IO) {
//                            if (isImageTheSame) return@withContext
//
//                            if (isEditableCard && editableCardOldVersion != null) {
//                                val oldFile = File(directory, editableCardOldVersion.path)
//                                oldFile.delete()
//                            }
//
//                            var fos: FileOutputStream? = null
//                            try {
//                                fos = FileOutputStream(file)
//                                image.compress(Bitmap.CompressFormat.PNG, 100, fos)
//                                fos.close()
//                            } catch (e: Exception) {
//                                Log.e("SAVE_IMAGE", e.message, e)
//                            }
//                        }
//                    }
//
//                    val newCardItem = ShopItem(
//                        uri = uri,
//                        path = file.path,
//                        title = state.modifiedCard?.title ?: "",
//                        id = state.modifiedCard?.id ?: UUID.randomUUID().toString()
//                    )
//
//                    val newCardList = if (editableCardOldVersion == null) {
//                        state.cardList.plus(newCardItem)
//                    } else {
//                        state.cardList.map { if (it.id == editableCardOldVersion.id) return@map newCardItem else return@map it }
//                    }
//
//
//                    viewModelScope.launch {
//                        val type: Type = object : TypeToken<List<ShopItemSP?>?>() {}.type
//                        val sharedPreference =
//                            context.getSharedPreferences("cards", Context.MODE_PRIVATE)
//                        val editor = sharedPreference.edit()
//                        val json = gson.toJson(newCardList.map {
//                            CardMapper.instance.shopItemToShopItemSP(it)
//                        }, type)
//                        editor.putString("items", json)
//                        editor.apply()
//
//                        state = state.copy(
//                            cardList = newCardList,
//                            modifiedCard = null,
//                            isAddNewCardModalOpen = false
//                        )
//                    }
//                }
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