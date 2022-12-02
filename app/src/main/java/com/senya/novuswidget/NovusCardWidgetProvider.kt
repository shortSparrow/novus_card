package com.senya.novuswidget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import android.widget.Toast


const val TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION"
const val EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM"

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

       if(TOAST_ACTION == intent?.action) {
           val viewIndex = intent.getIntExtra(EXTRA_ITEM, 0)
           Toast.makeText(context, "Item" + viewIndex + " selected", Toast.LENGTH_SHORT)
               .show()

           val intent2 =  Intent(context, MainActivity::class.java)
           intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
           intent2.putExtra("title", viewIndex)
           val pendingIntent = PendingIntent.getActivity(context, 0, intent2, FLAG_UPDATE_CURRENT)
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
//    // TODO update layout depend on widget width (N, NOVUS)
//    val views = RemoteViews(context.packageName, R.layout.novus_card_widget)
//    val intent = Intent(Intent.ACTION_VIEW)
//    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
//    views.setOnClickPendingIntent(R.id.root, pendingIntent)
//    // Instruct the widget manager to update the widget
//    appWidgetManager.updateAppWidget(appWidgetId, views)


    val intent = Intent(context, ListWidgetService::class.java)
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

    val views = RemoteViews(context.packageName, R.layout.novus_card_widget)

// // open app
//    val activityIntent = Intent(context, MainActivity::class.java)
//    activityIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
//    activityIntent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
//    val pendingIntent = PendingIntent.getActivity(
//        context, appWidgetId,
//        activityIntent, PendingIntent.FLAG_IMMUTABLE
//    )
//    views.setPendingIntentTemplate(R.id.list, pendingIntent)

    val activityIntent = Intent(context, NovusCardWidgetProvider::class.java)
    activityIntent.action = TOAST_ACTION
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0,
        activityIntent, 0
    )
    views.setPendingIntentTemplate(R.id.list, pendingIntent)

    views.setRemoteAdapter(R.id.list, intent)
    views.setEmptyView(R.id.list, R.id.empty_view)


    appWidgetManager.updateAppWidget(appWidgetId, views)
}