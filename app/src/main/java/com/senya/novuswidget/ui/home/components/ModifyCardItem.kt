package com.senya.novuswidget.ui.home.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.senya.novuswidget.domain.model.ModifiedShopItem
import com.senya.novuswidget.ui.core.Header
import com.senya.novuswidget.ui.extentions.opacityClick
import com.senya.novuswidget.ui.home.HomeAction

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ModifyCardItem(modifiedCard: ModifiedShopItem?, onAction: (HomeAction) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            onAction(HomeAction.AddTempModifiedImage(uri))
        }

    val imagePath = modifiedCard?.uri ?: modifiedCard?.path

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Header(
            title = "Add New Image",
            onBackButtonClick = { onAction(HomeAction.ToggleOpenAddNewCardModal(false)) },
            leftIcon = {
                if (modifiedCard?.path != null) {
                    Icon(
                        painter = painterResource(id = com.senya.novuswidget.R.drawable.delete),
                        contentDescription = "delete card",
                        tint = Color.Red,
                        modifier = Modifier.opacityClick { onAction(HomeAction.DeleteImage) }
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { keyboardController?.hide() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            TextField(
                value = modifiedCard?.title ?: "",
                onValueChange = { onAction(HomeAction.OnChangeCardTitle(it)) },
            )

            if (imagePath == null) {
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.padding(top = 30.dp)
                ) {
                    Text(text = "select image".uppercase())
                }
            }

            AsyncImage(
                model = imagePath,
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .padding(30.dp)
                    .opacityClick { galleryLauncher.launch("image/*") }
            )

            if (imagePath != null) {
                OutlinedButton(
                    onClick = { onAction(HomeAction.AddImage) },
                ) {
                    Text(text = "Save".uppercase())
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ModifyCardItemPreview(){
//    ModifyCardItem()
//}