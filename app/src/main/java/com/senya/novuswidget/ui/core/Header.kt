package com.senya.novuswidget.ui.core

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senya.novuswidget.R
import com.senya.novuswidget.ui.extentions.opacityClick

@Composable
fun Header(
    title: String,
    leftIcon: (@Composable () -> Unit)? = null,
    onBackButtonClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (onBackButtonClick != null) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "back button",
                modifier = Modifier.opacityClick { onBackButtonClick() }
            )
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        if (leftIcon != null) {
            leftIcon()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderPreview() {
    Header(
        title = "Home page",
        leftIcon = {
            Icon(
                painter = painterResource(id = R.drawable.sort),
                contentDescription = "change card order",
                modifier = Modifier
                    .opacityClick {}
                    .size(24.dp)
            )
        })
}
