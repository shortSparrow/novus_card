package com.senya.novuswidget.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.senya.novuswidget.MainActivity
import com.senya.novuswidget.R
import com.senya.novuswidget.ui.home.components.ModifyCardItem
import com.senya.novuswidget.ui.core.Header
import com.senya.novuswidget.ui.extentions.opacityClick
import com.senya.novuswidget.ui.home.components.CardItem
import com.senya.novuswidget.ui.home.components.ChangeCarOrder
import com.senya.novuswidget.ui.home.components.Footer
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun Home(state: HomeState, onAction: (HomeAction) -> Unit) {
    // TODO maybe on click open full screen image
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    )
    var backHandlingEnabled by remember { mutableStateOf(true) }
    BackHandler(backHandlingEnabled) {
        scope.launch {
            onAction(HomeAction.ToggleOpenAddNewCardModal(false))
        }
    }

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue.ordinal) {
        if (scaffoldState.bottomSheetState.currentValue.ordinal == 0) {
            onAction(HomeAction.ToggleOpenAddNewCardModal(false))
        }
    }

    LaunchedEffect(state.isAddNewCardModalOpen) {
        scope.launch {
            if (state.isAddNewCardModalOpen) {
                backHandlingEnabled = true
                scaffoldState.bottomSheetState.targetValue
                scaffoldState.bottomSheetState.expand()
            } else {
                scaffoldState.bottomSheetState.collapse()
                backHandlingEnabled = false
            }
        }
    }

    val initialListPosition = MainActivity.activityContext().intent?.getIntExtra("title", 0) ?: 0
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialListPosition)

    if (state.isOpenCardOrderModal) {
        Dialog(
            onDismissRequest = { onAction(HomeAction.SetIsOpenChangeOrderModal(false)) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            ChangeCarOrder(cardList = state.cardList.toMutableStateList(), onAction = onAction)
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            ModifyCardItem(modifiedCard = state.modifiedCard, onAction = onAction)
        },
        sheetPeekHeight = 0.dp,
    ) {
        Header(
            title = "Discount Card List",
            leftIcon = {
                Surface(Modifier.padding(end = 5.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.sort),
                        contentDescription = "change card order",
                        modifier = Modifier
                            .opacityClick { onAction(HomeAction.SetIsOpenChangeOrderModal(true)) }
                            .size(18.dp),

                        )
                }
            },
        )
        Column(modifier = Modifier.fillMaxSize()) {
            LazyRow(
                Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                state = listState,
                flingBehavior = ScrollableDefaults.flingBehavior()
            ) {
                items(state.cardList) { cardItem ->
                    CardItem(
                        cardItem = cardItem,
                        onAction = onAction
                    )
                }
            }

            Footer(onAction = onAction)
        }
    }

}

@Composable
@Preview(showBackground = true)
fun HomePreview() {
    Home(
        state = HomeState(),
        onAction = {}
    )
}