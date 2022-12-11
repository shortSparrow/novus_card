package com.senya.novuswidget.domain.model

import android.net.Uri

data class ModifiedShopItem(
    val uri: Uri? = null, // for case get image from gallery
    val path: String? = null, // for case load image from path
    val title: String = "",
    val id: String,
)
