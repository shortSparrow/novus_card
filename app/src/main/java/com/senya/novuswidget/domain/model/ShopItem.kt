package com.senya.novuswidget.domain.model

import android.net.Uri

data class ShopItem(
    val uri: Uri?,
    val path: String,
    val title: String = "",
    val id: String
)