package com.senya.novuswidget.ui

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.senya.novuswidget.HomeAction
import com.senya.novuswidget.HomeViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun Home(viewModel: HomeViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    )
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            viewModel.onAction(HomeAction.AddTempModifiedImage(uri))
        }

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue.ordinal) {
        if (scaffoldState.bottomSheetState.currentValue.ordinal == 0) {
            viewModel.onAction(HomeAction.ToggleOpenAddNewCardModal(false))
        }
    }

    LaunchedEffect(viewModel.state.isAddNewCardModalOpen) {
        scope.launch {
            if (viewModel.state.isAddNewCardModalOpen) {
                scaffoldState.bottomSheetState.targetValue
                scaffoldState.bottomSheetState.expand()
            } else {
                scaffoldState.bottomSheetState.collapse()
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Blue)
            ) {
                TextField(
                    value = viewModel.state.modifiedCard?.title ?: "",
                    onValueChange = { viewModel.onAction(HomeAction.OnChangeCardTitle(it)) })

                OutlinedButton(onClick = { galleryLauncher.launch("image/*") }) {
                    Text(text = "select image".uppercase())
                }

                AsyncImage(
                    model = viewModel.state.modifiedCard?.uri ?: viewModel.state.modifiedCard?.path,
                    contentDescription = null,
                )

                OutlinedButton(onClick = { viewModel.onAction(HomeAction.AddImage) }) {
                    Text(text = "Save".uppercase())
                }
            }
        },
        sheetPeekHeight = 0.dp,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyRow() {
                items(viewModel.state.cardList) { cardItem ->
                    Column() {
                        Text(text = cardItem.title)
                        AsyncImage(
                            model = cardItem.uri ?: cardItem.path,
                            contentDescription = null,
                            modifier = Modifier.combinedClickable(
                                onLongClick = {
                                    viewModel.onAction(HomeAction.SetModifiedCard(card = cardItem))
                                },
                                onClick = {}
                            )
                        )
                    }
                }
            }


            OutlinedButton(onClick = { viewModel.onAction(HomeAction.ToggleOpenAddNewCardModal(true)) }) {
                Text(text = "add new card".uppercase())
            }
        }
    }

}

@Composable
@Preview(showBackground = true)
fun HomePreview() {
    Home()
}