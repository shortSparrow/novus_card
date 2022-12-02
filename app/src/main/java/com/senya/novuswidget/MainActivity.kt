package com.senya.novuswidget

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

const val MAX_BRIGHTNESS = 255

class MainActivity : AppCompatActivity() {
    private var oldBrightness: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("XXXXXX", intent?.getIntExtra("title", -1).toString())
        try {
            oldBrightness =
                Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: SettingNotFoundException) {
            Log.e("Error", "Cannot access system brightness")
            e.printStackTrace()
        }
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


    override fun onNewIntent(intent: Intent?) {
        Log.d("XXXXXX_2", intent?.getIntExtra("title", -1).toString())
        super.onNewIntent(intent)
    }
}