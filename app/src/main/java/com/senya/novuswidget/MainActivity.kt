package com.senya.novuswidget

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.senya.novuswidget.ui.home.Home
import com.senya.novuswidget.ui.home.HomeViewModel
import com.senya.novuswidget.util.DEFAULT_INITIAL_CARD_POSITION
import com.senya.novuswidget.util.PRESSED_WIDGET_ITEM_INDEX


class MainActivity : AppCompatActivity() {
    var initialListPosition by mutableStateOf(DEFAULT_INITIAL_CARD_POSITION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialListPosition =
            intent?.getIntExtra(PRESSED_WIDGET_ITEM_INDEX, DEFAULT_INITIAL_CARD_POSITION)
                ?: DEFAULT_INITIAL_CARD_POSITION
        setContent {
            val vm: HomeViewModel = viewModel()
            val state = vm.state
            Home(state = state, onAction = vm::onAction, initialListPosition = initialListPosition)
        }

        setMaxBrightness()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val newInitialListPosition =
            intent?.getIntExtra(PRESSED_WIDGET_ITEM_INDEX, DEFAULT_INITIAL_CARD_POSITION)
                ?: DEFAULT_INITIAL_CARD_POSITION
        initialListPosition = newInitialListPosition
    }


    private fun setMaxBrightness() {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = 1f
        window.attributes = layoutParams
    }

}