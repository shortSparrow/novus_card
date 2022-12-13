package com.senya.novuswidget.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.senya.novuswidget.domain.model.ShopItem
import com.senya.novuswidget.extentios.move
import com.senya.novuswidget.ui.core.drag_end_drop.rememberDragDropListState
import com.senya.novuswidget.ui.home.HomeAction
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@Composable
fun ChangeCarOrder(cardList: SnapshotStateList<ShopItem>, onAction: (HomeAction) -> Unit) {
    val scope = rememberCoroutineScope()

    var overscrollJob by remember { mutableStateOf<Job?>(null) }

    val dragDropListState =
        rememberDragDropListState(onMove = { fromIndex, toIndex ->
            cardList.move(fromIndex, toIndex)
            scope.launch {
                onAction(HomeAction.SetNewCardOrder(cardList))
            }
        })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // TODO add back button
        Text(
            text = "Drag and drop for change order",
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDrag = { change, offset ->
                            change.consume()
                            dragDropListState.onDrag(offset)

                            if (overscrollJob?.isActive == true)
                                return@detectDragGesturesAfterLongPress

                            dragDropListState
                                .checkForOverScroll()
                                .takeIf { it != 0f }
                                ?.let {
                                    overscrollJob =
                                        scope.launch { dragDropListState.lazyListState.scrollBy(it) }
                                }
                                ?: run { overscrollJob?.cancel() }
                        },
                        onDragStart = { offset -> dragDropListState.onDragStart(offset) },
                        onDragEnd = { dragDropListState.onDragInterrupted() },
                        onDragCancel = { dragDropListState.onDragInterrupted() }
                    )
                },
            state = dragDropListState.lazyListState,
            verticalArrangement = Arrangement.Center
        ) {
            itemsIndexed(cardList) { index, item ->
                Row(
                    modifier = Modifier
                        .composed {
                            val offsetOrNull =
                                dragDropListState.elementDisplacement.takeIf {
                                    index == dragDropListState.currentIndexOfDraggedItem
                                }
                            Modifier
                                .graphicsLayer {
                                    translationY = offsetOrNull ?: 0f
                                }

                        }
                        .padding(bottom = 16.dp, top = if (index == 0) 16.dp else 0.dp)
                        .background(Color.LightGray, RoundedCornerShape(5.dp))
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = (index + 1).toString(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 15.dp)
                    )
                    AsyncImage(
                        model = item.uri ?: item.path,
                        contentDescription = "order card",
                        modifier = Modifier.size(50.dp)
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChangeCarOrderPreview() {
    ChangeCarOrder(
        cardList = listOf<ShopItem>().toMutableStateList(),
        onAction = {}
    )
}