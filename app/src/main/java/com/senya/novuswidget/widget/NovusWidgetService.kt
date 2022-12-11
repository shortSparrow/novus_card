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
            Log.d("XXXX", "onCreate")
            cardList.forEach {
                Log.d("XXXX_Item_Create", it.title)
                mWidgetItems.add(WidgetItem(it.title))
            }
        }

        override fun onDestroy() {
            mWidgetItems.clear()
        }

        override fun getCount(): Int {
            return cardList.size
        }

        // Given the position (index) of a WidgetItem in the array, use the item's text value in
        // combination with the app widget item XML file to construct a RemoteViews object.
        override fun getViewAt(position: Int): RemoteViews {

            // position will always range from 0 to getCount() - 1.
            // construct a remote views item based on our widget item xml file, and set the
            // text based on the position.
            val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
            rv.setTextViewText(R.id.text, mWidgetItems[position].title)

            // Next, we set a fill-intent which will be used to fill-in the pending intent template
            // which is set on the collection view in ListWidgetProvider.
            val extras = Bundle()
//            extras.putInt(NovusCardWidgetProvider.EXTRA_ITEM, position)
            extras.putInt(EXTRA_ITEM, position)
            val fillInIntent = Intent()
            fillInIntent.putExtras(extras)
            rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent)

            return rv
        }

        override fun getLoadingView(): RemoteViews {
            // You can create a custom loading view (for instance when getViewAt() is slow.) If you
            // return null here, you will get the default loading view.
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
            Log.d("XXXX", "onDataSetChanged")
            cardList.clear()
            cardList.addAll(GetCardsFromStorageUseCase().invoke())

            mWidgetItems.clear()
            cardList.forEach {
                Log.d("XXXX_Item_Update", it.title)
                mWidgetItems.add(WidgetItem(it.title))
            }
            // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
            // on the collection view corresponding to this factory. You can do heaving lifting in
            // here, synchronously. For example, if you need to process an image, fetch something
            // from the network, etc., it is ok to do it here, synchronously. The widget will remain
            // in its current state while work is being done here, so you don't need to worry about
            // locking up the widget.
        }
    }
}
