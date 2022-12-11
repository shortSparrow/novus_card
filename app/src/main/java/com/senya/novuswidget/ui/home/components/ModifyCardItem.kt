package com.senya.novuswidget.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import com.senya.novuswidget.domain.model.ModifiedShopItem
import com.senya.novuswidget.ui.home.HomeAction

@Composable
fun ModifyCardItem(modifiedCard: ModifiedShopItem?, onAction: (HomeAction) -> Unit) {
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            onAction(HomeAction.AddTempModifiedImage(uri))
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        TextField(
            value = modifiedCard?.title ?: "",
            onValueChange = { onAction(HomeAction.OnChangeCardTitle(it)) })

        OutlinedButton(onClick = { galleryLauncher.launch("image/*") }) {
            Text(text = "select image".uppercase())
        }

        AsyncImage(
            model = modifiedCard?.uri ?: modifiedCard?.path,
            contentDescription = null,
        )

        OutlinedButton(onClick = { onAction(HomeAction.AddImage) }) {
            Text(text = "Save".uppercase())
        }
    }
}