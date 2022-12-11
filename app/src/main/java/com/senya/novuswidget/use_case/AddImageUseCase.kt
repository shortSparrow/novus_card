package com.senya.novuswidget.use_case

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.gson.reflect.TypeToken
import com.senya.novuswidget.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Type

class AddImageUseCase() {
    private val context = MainActivity.activityContext()
    private val directory: File =
        ContextWrapper(context).getDir("discount_cards", Context.MODE_PRIVATE)

    operator fun invoke(modifiedCard: ModifiedShopItem?, cardList: List<ShopItem>): List<ShopItem> {
        if (modifiedCard == null) return cardList

        // if modifiedCard.uri == null this old file
        val fileName = if (modifiedCard.uri != null) "${System.currentTimeMillis()}.jpg" else null
        val file = if (fileName != null) File(directory, fileName) else null
        val editableCardOldVersion = cardList.find { it.id == modifiedCard.id }

        if (file != null) {
            saveImage(
                file = file,
                modifiedCard = modifiedCard,
                editableCardOldVersion = editableCardOldVersion
            )
        }

        // file.path == null this means file was not created, because image didn't updated
        val newCardItemPath = (file?.path ?: editableCardOldVersion?.path).toString()

        val newCardItem = ShopItem(
            uri = modifiedCard.uri,
            path = newCardItemPath,
            title = modifiedCard.title,
            id = modifiedCard.id
        )

        val newCardList = if (editableCardOldVersion == null) {
            cardList.plus(newCardItem)
        } else {
            cardList.map { if (it.id == editableCardOldVersion.id) return@map newCardItem else return@map it }
        }

        updateSharedPreferencesData(newCardList)

        return newCardList
    }

    private fun saveImage(
        file: File,
        modifiedCard: ModifiedShopItem,
        editableCardOldVersion: ShopItem?
    ) {
        if (modifiedCard.uri == null) return
        val isImageWasReplaced = editableCardOldVersion?.path != null
        val image = getImage(modifiedCard.uri)

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                // if user modified card and replaced image delete old one
                if (isImageWasReplaced) {
                    val oldFile = File(editableCardOldVersion!!.path)
                    oldFile.delete()
                }

                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(file)
                    image.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    fos.close()
                } catch (e: Exception) {
                    Log.e("SAVE_IMAGE", e.message, e)
                }
            }
        }
    }

    private fun getImage(uri: Uri): Bitmap {
        val stream = context.contentResolver.openInputStream(uri)
        val image = BitmapFactory.decodeStream(stream)
        if (!directory.exists()) {
            directory.mkdir()
        }

        return image
    }

    private fun updateSharedPreferencesData(newCardList: List<ShopItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            val type: Type = object : TypeToken<List<ShopItemSP?>?>() {}.type
            val sharedPreference =
                context.getSharedPreferences("cards", Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()
            val json = gson.toJson(newCardList.map {
                CardMapper.instance.shopItemToShopItemSP(it)
            }, type)
            editor.putString("items", json)
            editor.apply()
        }
    }

}



