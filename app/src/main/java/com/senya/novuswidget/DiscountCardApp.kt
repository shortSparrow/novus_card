package com.senya.novuswidget

import android.app.Application
import android.content.Context

class DiscountCardApp : Application() {
    init {
        instance = this
    }


    companion object {
        private var instance: DiscountCardApp? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}