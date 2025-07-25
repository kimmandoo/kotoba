package com.kotoba.app

import android.app.Application
import com.kotoba.di.ContextProvider

class KotobaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextProvider.initialize(this)
    }
}
