package com.senya.novuswidget.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.senya.novuswidget.R
import com.senya.novuswidget.use_case.GetCardsFromStorageUseCase
import com.senya.novuswidget.util.EXTRA_ITEM


data class WidgetItem(val title: String)

class ListWidgetService : RemoteViewsService() {
    val cardList = GetCardsFromStorageUseCase().invoke().toMutableList()

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ListRemoteViewsFactory(applicationContext, intent)
    }

    internal inner class ListRemoteViewsFactory(private val mContext: Context, intent: Intent) :
        RemoteViewsFactory {
        private val mWidgetItems: MutableList<WidgetItem> = ArrayList<WidgetItem>()
        private val mAppWidgetId: Int

        init {
            mAppWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        override fun onCreate() {
            cardList.forEach {
                mWidgetItems.add(WidgetItem(it.title))
            }
        }

        override fun onDestroy() {
            mWidgetItems.clear()
        }

        override fun getCount(): Int {
            return cardList.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
            rv.setTextViewText(R.id.text, mWidgetItems[position].title)

            val extras = Bundle()
            extras.putInt(EXTRA_ITEM, position)
            val fillInIntent = Intent()
            fillInIntent.putExtras(extras)
            rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent)

            return rv
        }

        override fun getLoadingView(): RemoteViews {
            return RemoteViews(this@ListWidgetService.packageName, R.layout.novus_card_widget)
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        override fun onDataSetChanged() {
            cardList.clear()
            cardList.addAll(GetCardsFromStorageUseCase().invoke())

            mWidgetItems.clear()
            cardList.forEach {
                mWidgetItems.add(WidgetItem(it.title))
            }
        }
    }
}
