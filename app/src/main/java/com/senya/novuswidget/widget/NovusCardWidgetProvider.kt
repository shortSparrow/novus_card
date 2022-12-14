package com.senya.novuswidget.widget

import android.app.PendingIntent
import android.app.PendingIntent.*
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.senya.novuswidget.MainActivity
import com.senya.novuswidget.R
import com.senya.novuswidget.util.EXTRA_ITEM
import com.senya.novuswidget.util.PRESSED_WIDGET_ITEM_INDEX
import com.senya.novuswidget.util.SELECT_CARD_ACTION


class NovusCardWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (SELECT_CARD_ACTION == intent?.action) {
            val viewIndex = intent.getIntExtra(EXTRA_ITEM, 0)
            val intent2 = Intent(context, MainActivity::class.java)
            intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent2.putExtra(PRESSED_WIDGET_ITEM_INDEX, viewIndex)
            val pendingIntent = getActivity(context, 0, intent2, FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)
            pendingIntent.send()
        }
        super.onReceive(context, intent)
    }


    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val intent = Intent(context, ListWidgetService::class.java)
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

    val views = RemoteViews(context.packageName, R.layout.novus_card_widget)

    val activityIntent = Intent(context, NovusCardWidgetProvider::class.java)
    activityIntent.action = SELECT_CARD_ACTION
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0,
        activityIntent, FLAG_MUTABLE
    )
    views.setPendingIntentTemplate(R.id.widget_discount_card_list, pendingIntent)

    views.setRemoteAdapter(R.id.widget_discount_card_list, intent)
    views.setEmptyView(R.id.widget_discount_card_list, R.id.empty_view)

    val emptyViewIntent = Intent(context, MainActivity::class.java)
    emptyViewIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    emptyViewIntent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
    val pendingIntentEmptyView = PendingIntent.getActivity(
        context, appWidgetId,
        emptyViewIntent, FLAG_MUTABLE
    )
    views.setOnClickPendingIntent(R.id.empty_view, pendingIntentEmptyView)


    appWidgetManager.updateAppWidget(appWidgetId, views)
}