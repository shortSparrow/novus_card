package com.senya.novuswidget

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.Log
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

const val MAX_BRIGHTNESS = 255

class MainActivity : AppCompatActivity() {
    private var oldBrightness: Int? = null
    var initialListPosition by mutableStateOf(DEFAULT_INITIAL_CARD_POSITION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialListPosition = intent?.getIntExtra(PRESSED_WIDGET_ITEM_INDEX, DEFAULT_INITIAL_CARD_POSITION) ?: DEFAULT_INITIAL_CARD_POSITION
        setContent {
            val vm: HomeViewModel = viewModel()
            val state = vm.state
            Home(state = state, onAction = vm::onAction, initialListPosition = initialListPosition)
        }

        try {
            oldBrightness =
                Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: SettingNotFoundException) {
            Log.e("Error", "Cannot access system brightness")
            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val newInitialListPosition = intent?.getIntExtra(PRESSED_WIDGET_ITEM_INDEX, DEFAULT_INITIAL_CARD_POSITION) ?: DEFAULT_INITIAL_CARD_POSITION
        initialListPosition = newInitialListPosition
    }

    override fun onStop() {
        super.onStop()
        if (checkWriteSettingsPermission()) {
            oldBrightness?.let { setBrightness(it) }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!checkWriteSettingsPermission()) {
            openAndroidPermissionsMenu()
        } else {
            setBrightness(MAX_BRIGHTNESS)
        }
    }

    private fun checkWriteSettingsPermission(): Boolean {
        var canWrite = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            canWrite = Settings.System.canWrite(this)
        }
        return canWrite
    }

    private fun openAndroidPermissionsMenu() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = Uri.parse("package:" + this.packageName)
        startActivity(intent)
    }


    private fun setBrightness(value: Int) {
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, value); }

}