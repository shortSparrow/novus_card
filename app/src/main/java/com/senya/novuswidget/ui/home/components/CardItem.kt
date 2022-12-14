package com.senya.novuswidget.ui.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.senya.novuswidget.domain.model.ShopItem
import com.senya.novuswidget.ui.home.HomeAction

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardItem(cardItem: ShopItem, onAction: (HomeAction) -> Unit) {
    val configuration = LocalConfiguration.current
    val cardItemSize = configuration.screenWidthDp - 20

    Column(
        modifier = Modifier.padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = cardItem.title,
            modifier = Modifier.padding(vertical = 20.dp),
            fontWeight = FontWeight.Bold
        )
        AsyncImage(
            model = cardItem.uri ?: cardItem.path,
            contentDescription = null,
            modifier = Modifier
                .combinedClickable(
                    onLongClick = {
                        onAction(HomeAction.SetModifiedCard(card = cardItem))
                    },
                    onClick = {}
                )
                .size(cardItemSize.dp)
                .background(Color.Black)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun CardItemPreview() {
    CardItem(cardItem = ShopItem(
        uri = null,
        path = "",
        id = "",
        title = "NOVUS"
    ), onAction = {})
}