package com.senya.novuswidget.ui.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.senya.novuswidget.ui.home.HomeAction

@Composable
fun Footer(onAction: (HomeAction) -> Unit) {
    fun handleAddNewCardClick() {
        onAction(HomeAction.ToggleOpenAddNewCardModal(true))
    }

    Box(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        OutlinedButton(onClick = ::handleAddNewCardClick) {
            Text(text = "add new card".uppercase())
        }
    }
}