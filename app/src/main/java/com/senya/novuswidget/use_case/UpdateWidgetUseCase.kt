package com.senya.novuswidget.use_case

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import com.senya.novuswidget.MainActivity
import com.senya.novuswidget.R
import com.senya.novuswidget.widget.NovusCardWidgetProvider

class UpdateWidgetUseCase {
    private val context = MainActivity.activityContext()

    fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(context)

        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                NovusCardWidgetProvider::class.java
            )
        )
        appWidgetManager.notifyAppWidgetViewDataChanged(
            appWidgetIds,
            R.id.widget_discount_card_list
        )
    }
}